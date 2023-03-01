package Project.SangCom.chat.service;


import Project.SangCom.chat.domain.ChatMessage;
import Project.SangCom.chat.domain.ChatRoom;
import Project.SangCom.chat.domain.ChatUserMap;
import Project.SangCom.chat.dto.ChatRoomRequest;
import Project.SangCom.chat.dto.ChatRoomResponse;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

        assertThat(chatMessage.getContent()).isEqualTo("direct message test");
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










    private User getUser(String username, String nickname, String email) {
        User user = User.builder()
                .username(username)
                .nickname(nickname)
                .email(email)
                .role(Role.STUDENT.getKey())
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
                .content("direct message test")
                .isDirect(1)
                .build();
    }
}