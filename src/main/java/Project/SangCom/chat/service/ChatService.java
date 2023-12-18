package Project.SangCom.chat.service;

import Project.SangCom.chat.domain.ChatMessage;
import Project.SangCom.chat.domain.ChatRoom;
import Project.SangCom.chat.domain.ChatUserMap;
import Project.SangCom.chat.dto.*;
import Project.SangCom.chat.repository.ChatMessageRepository;
import Project.SangCom.chat.repository.ChatRepositoryImpl;
import Project.SangCom.chat.repository.ChatRoomRepository;
import Project.SangCom.chat.repository.ChatUserMapRepository;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.domain.embedded.TeacherInfo;
import Project.SangCom.user.service.UserService;
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final UserService userService;
    private final ChatRepositoryImpl chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository messageRepository;
    private final ChatUserMapRepository chatUserMapRepository;



    /**
     * ChatRoom 새로 생성 후, 새로 생긴 톡방의 Long(PK)를 반환
     * @param sender chatRoom 생성을 요청하는 사용자
     * @param chatRoomRequest chatRoom 생성에 필요한 인자들을 담은 객체
     */
    @Transactional
    public Long saveChatRoom(User sender, ChatRoomRequest chatRoomRequest){
        // chatRoomRequest에 content(첫 채팅 메시지)가 담겨져 있지 않을 때 에러 발생
        if (Objects.equals(chatRoomRequest.getContent(), "") || chatRoomRequest.getContent() == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER);
        }

        // 첫 메시지
        ChatMessage chatMessage = ChatMessage.builder()
                .senderId(sender.getId())
                .senderName(sender.getUsername())
                .content(chatRoomRequest.getContent())
                .build();

        // ChatUserMap 생성
        List<ChatUserMap> maps = new ArrayList<>();

        maps.add(ChatUserMap.createChatUserMap(sender)); // sender
        getChatUserMapList(maps, chatRoomRequest); // receivers

        // ChatRoom 생성
        ChatRoom chatRoom = ChatRoom.createChatRoom(chatMessage, maps, chatRoomRequest.getIsDirect());
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        return savedChatRoom.getId();
    }

    /**
     * 사용자들이 포함된 ChatRoom이 기존에 있는지 확인 후
     * 있다면 해당 ChatRoom의 PK를, 없다면 -1을 return
     */
    public Long findJoinedChatPK(User sender, ChatRoomRequest chatRoomRequest){
        List<Long> userIds = new ArrayList<>();
        for (Long id : chatRoomRequest.getReceiverId()) {
            userIds.add(id);
        }
        userIds.add(sender.getId());

        List<Long> joinedChat = chatRepository.findJoinedChat(userIds);
        if (joinedChat.isEmpty()){
            return (long) -1;
        }
        return joinedChat.get(0);
    }

    public ChatRoomResponse findJoinedChat(User user, List<Long> userIds, Pageable pageable){
        List<Long> joinedChat = chatRepository.findJoinedChat(userIds);
        if (joinedChat.isEmpty()){
            // 비어있는 응답 객체 반환
            return new ChatRoomResponse();
        }

        // 메세지 내용까지 들어있는 응답 객체 반환
        return convertToDetailChatResponse(user, findChatRoomById(joinedChat.get(0)), pageable);
    }

    /**
     * request에 있는 모든 receiver들을 ChatUserMap에 user 설정하여 리스트에 담고 반환
     * @param maps 추가해야하는 ChatUserMap 리스트
     * @param request 채팅방 개설 시의 요청 객체
     */
    private List<ChatUserMap> getChatUserMapList(List<ChatUserMap> maps, ChatRoomRequest request){
        List<Long> receiverId = request.getReceiverId();
        for (Long id : receiverId) {
            maps.add(ChatUserMap.createChatUserMap(userService.findUserById(id)));
        }
        return maps;
    }


    /**
     * UserId와 일치하는 ChatRoom 모두 조회 후, ChatRoomResponse로 변환하여 반환
     * @param user 채팅 목록을 조회 할 사용자 대상자
     */
    public List<ChatRoomResponse> getJoinedChatRoomList(User user){
        List<ChatRoom> chatRooms = chatUserMapRepository.getMapListByUserId(user.getId());

        return chatRooms.stream().map(c -> convertToChatRoomResponse(c))
                .collect(Collectors.toList());
    }

    /**
     * id를 통해 ChatRoom 객체를 반환
     * @param id 찾고자 하고자 하는 ChatRoom PK
     */
    public ChatRoom findChatRoomById(Long id){
        return chatRoomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));
    }


    /**
     * 특정 톡방(ChatRoom)에 메시지(ChatMessage)를 전달
     * isDirect 여부를 확인하고
     * @param user
     * @param chatMessageRequest 전송할 메시지를 담은 요청 객체
     * @api /api/message/{chatRoomId}
     */
    @Transactional
    public ChatMessageResponse writeChatMessage(User user, Long roomId, ChatMessageRequest chatMessageRequest){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));

        // ChatMessageRequest의 내용을 토대로 ChatMessage 객체 생성 및 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .senderId(user.getId())
                .senderName(convertNameFormat(user))
                .content(chatMessageRequest.getContent())
                .build();
        ChatMessage savedMessage = messageRepository.save(chatMessage);
        chatRoom.addChatMessage(savedMessage); //연관관계 설정

        return convertToChatMessageResponse(user, savedMessage);
    }


    /**
     * 특정 톡방(ChatRoom)에 있는 메세지들을 페이징하여 반환
     * @param user 현재 사용자 정보
     * @param roomId 메시지 리스트를 받고자 하는 ChatRoom의 PK
     */
    public Slice<ChatMessageResponse> getChatMessageList(User user, Long roomId, Pageable pageable){
        Slice<ChatMessage> chatMessages = messageRepository.getChatMessageList(roomId, pageable);
        return chatMessages.map(m -> convertToChatMessageResponse(user, m));
    }


    /**
     * 채팅방에 존재하는 사용자인지 확인 후, soft-delete 진행
     * 참가한 모든 사용자가 soft-delete 한 경우에는 DB에서 삭제
     * @param user 채팅방을 삭제하고자하는 사용자
     * @param roomId 삭제하고자하는 채팅방의 PK
     */
    @Transactional
    public void deleteChatRoom(User user, Long roomId){

    }


    /**
     * 교사가 본인의 상태 메시지를 변경
     * 교사가 아닌 경우 or 본인이 아닌 경우 예외 발생
     */
    @Transactional
    public TeacherProfileDTO changeStatusMessage(User user, TeacherProfileDTO teacherProfileDTO) {
        // 교사가 아니거나, 본인이 아니면 예외 발생
        if (!user.getRole().contains("TEACHER") || !Objects.equals(user.getId(), teacherProfileDTO.getId())){
            throw new BusinessException(ErrorCode.NO_AUTHORITY);
        }

        String newStatusMessage = teacherProfileDTO.getStatusMessage();

        // statusMessage가 30을 초과한다면 예외 발생
        if (newStatusMessage.length() > 30){
            throw new BusinessException(ErrorCode.LENGTH_EXCEED);
        }

        // 상태메시지 갱신
        user.updateStatusMessage(newStatusMessage);
        return convertToTeacherProfileDTO(user);
    }







    // User를 TeacherProfileDTO(카드에 필요한 정보)로 변환하여 반환
    private TeacherProfileDTO convertToTeacherProfileDTO(User user){
        TeacherInfo teacherInfo = user.getTeacherInfo();
        String chargeInfo = teacherInfo.getChargeGrade() + "학년 ";

        return TeacherProfileDTO.builder()
                .id(user.getId())
                .name(user.getUsername())
                .chargeClass(chargeInfo)
                .chargeSubject(teacherInfo.getChargeSubject())
                .statusMessage(teacherInfo.getStatusMessage())
                .build();
    }

    // ChatRoom(채팅방)를 ChatRoomResponse(채팅방 응답 DTO)로 변환하여 반환
    public ChatRoomResponse convertToChatRoomResponse(ChatRoom chatRoom){
        List<ChatUserDTO> infoList = chatUserMapRepository.getUserByRoomId(chatRoom.getId())
                .stream()
                .map(u -> ChatUserDTO.builder()
                        .userId(u.getId())
                        .displayName(u.getUsername())
                        .build())
                .collect(Collectors.toList());

        List<ChatMessage> chatMessages = chatRoom.getChatMessages();

        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .isDirect(chatRoom.getIsDirect())
                .lastMessage(chatMessages.get(chatMessages.size()-1).getContent())
                .userInfo(infoList)
                .build();
    }
    public ChatRoomResponse convertToDetailChatResponse(User user, ChatRoom chatRoom, Pageable pageable){
        // 참가자 명단 변환
        List<ChatUserDTO> infoList = chatUserMapRepository.getUserByRoomId(chatRoom.getId())
                .stream()
                .map(u -> ChatUserDTO.builder()
                        .userId(u.getId())
                        .displayName(u.getUsername())
                        .build())
                .collect(Collectors.toList());

        // ChatRoom의 ChatMessage를 변환
        Slice<ChatMessageResponse> messageList = getChatMessageList(user, chatRoom.getId(), pageable);

        // ChatRoom 정보 & ChatRoom의 메시지 반환
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .isDirect(chatRoom.getIsDirect())
                .lastMessage(messageList.getContent().get(0).getContent())
                .messageList(messageList)
                .userInfo(infoList)
                .build();
    }


    // ChatMessage를 응답 객체(ChatMessageResponse)로 변환하여 반환
    private ChatMessageResponse convertToChatMessageResponse(User user, ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .id(chatMessage.getId())
                .content(chatMessage.getContent())
                .author(chatMessage.getSenderName())
                .isOwner(checkIsMessageOwner(user, chatMessage))
                .createdDate(chatMessage.getCreatedDate())
                .build();
    }
    private String convertNameFormat(User user){
        String name = user.getUsername();
        if (user.getRole().contains("STUDENT")){
            name += "(" + user.getStudentInfo().getClasses() + "반)";
        }
        return name;
    }
    // 사용자가 작성한 chatMessage인지 여부 확인- 1:true, 0:false
    private int checkIsMessageOwner(User user, ChatMessage chatmessage){
        if (Objects.equals(user.getId(), chatmessage.getSenderId())){
            return 1;
        }
        return 0;
    }
}
