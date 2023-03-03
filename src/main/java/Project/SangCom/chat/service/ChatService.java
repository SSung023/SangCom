package Project.SangCom.chat.service;

import Project.SangCom.chat.domain.ChatMessage;
import Project.SangCom.chat.domain.ChatRoom;
import Project.SangCom.chat.domain.ChatUserMap;
import Project.SangCom.chat.dto.*;
import Project.SangCom.chat.repository.ChatMessageRepository;
import Project.SangCom.chat.repository.ChatRoomRepository;
import Project.SangCom.chat.repository.ChatUserMapRepository;
import Project.SangCom.user.domain.User;
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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final UserService userService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository messageRepository;
    private final ChatUserMapRepository chatUserMapRepository;


    /**
     * ChatRoom을 새로 생성
     * @param sender chatRoom 생성을 요청하는 사용자
     * @param chatRoomRequest chatRoom 생성에 필요한 인자들을 담은 객체
     */
    @Transactional
    public Long saveChatRoom(User sender, ChatRoomRequest chatRoomRequest){
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
        List<ChatUserMap> mapList = chatUserMapRepository.getChatUserMapList(user.getId());

        List<ChatRoom> chatRooms = mapList
                .stream()
                .map(c -> c.getChatRoom()).toList();

        return chatRooms.stream().map(c -> convertToChatRoomResponse(c, mapList)).toList();
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










    // ChatRoom(채팅방)를 ChatRoomResponse(채팅방 응답 DTO)로 변환하여 반환
    private ChatRoomResponse convertToChatRoomResponse(ChatRoom chatRoom, List<ChatUserMap> mapList){
        List<ChatMessage> chatMessages = chatRoom.getChatMessages();
        List<ChatUserDTO> infoList = getChatUserInfoList(mapList);

        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .isDirect(chatRoom.getIsDirect())
                .lastMessage(chatMessages.get(chatMessages.size()-1).getContent())
                .userInfo(infoList)
                .build();
    }
    // ChatRoom(채팅방)에 참가하고 있는 사용자 정보를 ChatUserDTO로 변환하여 반환
    private List<ChatUserDTO> getChatUserInfoList(List<ChatUserMap> mapList) {
        List<ChatUserDTO> chatUserDTOS = new ArrayList<>();

        for (ChatUserMap map : mapList) {
            chatUserDTOS.add(ChatUserDTO.builder()
                            .userId(map.getUser().getId())
                            .displayName(map.getUser().getUsername())
                            .build());
        }
        return chatUserDTOS;
    }

    // ChatMessage를 응답 객체(ChatMessageResponse)로 변환하여 반환
    private ChatMessageResponse convertToChatMessageResponse(User user, ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
                .id(chatMessage.getId())
                .content(chatMessage.getContent())
                .author(chatMessage.getSenderName())
                .isOwner(checkIsMessageOwner(user, chatMessage))
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
