package Project.SangCom.chat.service;


import Project.SangCom.chat.domain.ChatMessage;
import Project.SangCom.chat.domain.ChatRoom;
import Project.SangCom.chat.domain.ChatUserMap;
import Project.SangCom.chat.dto.ChatMessageRequest;
import Project.SangCom.chat.dto.ChatMessageResponse;
import Project.SangCom.chat.dto.ChatRoomRequest;
import Project.SangCom.chat.dto.ChatRoomResponse;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.domain.embedded.StudentInfo;
import Project.SangCom.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
class ChatServiceTest {
    @Autowired
    UserRepository userRepository;
    @Autowired ChatService chatService;


    @Test
    @DisplayName("ChatRoom 생성 시 연관관계까지 설정이 되어야 한다.")
    public void createChatRoom(){
        //given
        User user1 = getUser("username1", "nickname1", "test1@naver.com");
        User user2 = getUser("username2", "nickname2", "test2@naver.com");
        ChatRoomRequest chatRoomRequest = getChatRoomRequest(user2.getId());
        
        //when
        Long saveChatRoomId = chatService.saveChatRoom(user1, chatRoomRequest);
        ChatRoom chatRoomById = chatService.findChatRoomById(saveChatRoomId);

        ChatMessage chatMessage = chatRoomById.getChatMessages().get(0);
        List<ChatUserMap> maps = chatRoomById.getChatUserMaps();

        //then
        assertThat(chatRoomById.getIsDirect()).isEqualTo(1);

        assertThat(chatMessage.getContent()).isEqualTo("first direct message");
        assertThat(chatMessage.getSenderId()).isEqualTo(user1.getId());
        assertThat(chatMessage.getSenderName()).isEqualTo(user1.getUsername());

        assertThat(maps.get(0).getUser().getId()).isEqualTo(user1.getId());
        assertThat(maps.get(1).getUser().getId()).isEqualTo(user2.getId());
    }

    @Test
    @DisplayName("사용자가 참가한 상태의 ChatRoom 반환")
    public void returnChatRoom_ThatUserJoined(){
        //given
        User user1 = getUser("username1", "nickname1", "test1@naver.com");
        User user2 = getUser("username2", "nickname2", "test2@naver.com");
        ChatRoomRequest chatRoomRequest = getChatRoomRequest(user2.getId());

        //when
        Long saveChatRoomId = chatService.saveChatRoom(user1, chatRoomRequest);
        ChatRoom chatRoomById = chatService.findChatRoomById(saveChatRoomId);

        List<ChatRoomResponse> chatRooms1 = chatService.getJoinedChatRoomList(user1);
        List<ChatRoomResponse> chatRooms2 = chatService.getJoinedChatRoomList(user2);

        //then
        ChatRoomResponse room1 = chatRooms1.get(0);
        assertThat(room1.getId()).isEqualTo(chatRoomById.getId());
        assertThat(room1.getIsDirect()).isEqualTo(chatRoomById.getIsDirect());

        ChatRoomResponse room2 = chatRooms2.get(0);
        assertThat(room2.getId()).isEqualTo(chatRoomById.getId());
        assertThat(room2.getIsDirect()).isEqualTo(chatRoomById.getIsDirect());
    }

    @Test
    @DisplayName("chatMessageRequest를 받아서 ChatMessage로 저장하고 ChatMessageResponse로 반환한다.")
    public void canConvertToChatMessage(){
        //given
        User user1 = getUser("username1", "nickname1", "test1@naver.com");
        User user2 = getUser("username2", "nickname2", "test2@naver.com");

        ChatRoom chatRoom = saveAndGetChatRoom(user1, user2);

        //when
        ChatMessageRequest messageRequest = new ChatMessageRequest("chat message1");
        ChatMessageResponse messageResponse = chatService.writeChatMessage(user1, chatRoom.getId(), messageRequest);

        //then
        assertThat(messageResponse.getContent()).isEqualTo(messageRequest.getContent());
        assertThat(messageResponse.getIsOwner()).isEqualTo(1);
        assertThat(messageResponse.getAuthor()).isEqualTo("username1(2반)");
    }
    
    @Test
    @DisplayName("저장한 ChatMessage는 ChatRoom에 연관관계로 설정되어야 한다.")
    public void ChatMessage_ShouldSetRelation_WithChatRoom(){
        //given
        User user1 = getUser("username1", "nickname1", "test1@naver.com");
        User user2 = getUser("username2", "nickname2", "test2@naver.com");

        ChatRoom chatRoom = saveAndGetChatRoom(user1, user2);

        //when
        ChatMessageRequest messageRequest = new ChatMessageRequest("chat message1");
        chatService.writeChatMessage(user1, chatRoom.getId(), messageRequest);
        
        //then
        assertThat(chatRoom.getChatMessages().size()).isEqualTo(2);
        assertThat(chatRoom.getIsDirect()).isEqualTo(1);
    }

    @Test
    @DisplayName("ChatRoom에 있는 ChatMessage들을 페이징하여 ChatMessageResponse로 받을 수 있다.")
    public void canGetChatMessageResponseLists(){
        //given
        User user1 = getUser("username1", "nickname1", "test1@naver.com");
        User user2 = getUser("username2", "nickname2", "test2@naver.com");

        ChatRoom chatRoom = saveAndGetChatRoom(user1, user2);

        //when
        ChatMessageRequest messageRequest1 = new ChatMessageRequest("chat message1");
        ChatMessageRequest messageRequest2 = new ChatMessageRequest("chat message2");
        ChatMessageRequest messageRequest3 = new ChatMessageRequest("chat message3");

        ChatMessageResponse message1 = chatService.writeChatMessage(user1, chatRoom.getId(), messageRequest1);
        ChatMessageResponse message2 =chatService.writeChatMessage(user2, chatRoom.getId(), messageRequest2);
        ChatMessageResponse message3 =chatService.writeChatMessage(user1, chatRoom.getId(), messageRequest3);

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        Slice<ChatMessageResponse> chatMessageList = chatService.getChatMessageList(user1, chatRoom.getId(), pageRequest);

        //then
        assertThat(chatMessageList.getContent().size()).isEqualTo(4);
        assertThat(chatMessageList.getContent().get(0).getContent()).isEqualTo(message3.getContent());
        assertThat(chatMessageList.getContent().get(1).getContent()).isEqualTo(message2.getContent());
        assertThat(chatMessageList.getContent().get(2).getContent()).isEqualTo(message1.getContent());
        assertThat(chatMessageList.getContent().get(3).getContent()).isEqualTo("first direct message");

    }












    private User getUser(String username, String nickname, String email) {
        User user = User.builder()
                .username(username)
                .nickname(nickname)
                .email(email)
                .role(Role.STUDENT.getKey())
                .studentInfo(new StudentInfo("1", "2", "3"))
                .build();
        return userRepository.save(user);
    }
    private ChatRoomRequest getChatRoomRequest(Long... userIds) {
        List<Long> ids = new ArrayList<>();
        for (Long id : userIds) {
            ids.add(id);
        }
        return ChatRoomRequest.builder()
                .receiverId(ids)
                .content("first direct message")
                .isDirect(1)
                .build();
    }
    private ChatRoom saveAndGetChatRoom(User user1, User user2){
        ChatRoomRequest chatRoomRequest = getChatRoomRequest(user2.getId());
        Long saveChatRoomId = chatService.saveChatRoom(user1, chatRoomRequest);
        return chatService.findChatRoomById(saveChatRoomId);
    }
}