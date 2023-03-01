package Project.SangCom.chat.service;

import Project.SangCom.chat.domain.ChatMessage;
import Project.SangCom.chat.domain.ChatRoom;
import Project.SangCom.chat.domain.ChatUserMap;
import Project.SangCom.chat.dto.ChatRoomRequest;
import Project.SangCom.chat.dto.ChatRoomResponse;
import Project.SangCom.chat.dto.ChatUserDTO;
import Project.SangCom.chat.repository.ChatRoomRepository;
import Project.SangCom.chat.repository.ChatUserMapRepository;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.service.UserService;
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final UserService userService;
    private final ChatRoomRepository chatRoomRepository;
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

}
