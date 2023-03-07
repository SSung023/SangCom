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
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ErrorCode;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        assertThat(room1.getUserInfo().size()).isEqualTo(2);

        ChatRoomResponse room2 = chatRooms2.get(0);
        assertThat(room2.getId()).isEqualTo(chatRoomById.getId());
        assertThat(room2.getIsDirect()).isEqualTo(chatRoomById.getIsDirect());
        assertThat(room2.getUserInfo().size()).isEqualTo(2);
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

    @Test
    @DisplayName("특정 사용자들이 참여하고 있는 ChatRoom의 PK 반환할 수 있다.")
    public void returnChatRoomPK_ThatUserJoined(){
        //given
        User user1 = getUser("username1", "nickname1", "test1@naver.com");
        User user2 = getUser("username2", "nickname2", "test2@naver.com");
        User user3 = getUser("username3", "nickname3", "test3@naver.com");
        ChatRoomRequest chatRoomRequest = getChatRoomRequest(user2.getId(), user3.getId());

        //when
        Long saveChatRoomId = chatService.saveChatRoom(user1, chatRoomRequest);
        Long chatroomExist = chatService.findJoinedChatPK(user1, chatRoomRequest);

        //then
        assertThat(chatroomExist).isEqualTo(saveChatRoomId);

        ChatRoom foundRoom = chatService.findChatRoomById(chatroomExist);
        assertThat(foundRoom.getIsDirect()).isEqualTo(0);
    }
    @Test
    @DisplayName("이미 존재하는 톡방의 경우 1 이상의 정수를 반환한다.")
    public void shouldReturnMoreThan1_whenChatExist(){
        //given
        User user1 = getUser("username1", "nickname1", "test1@naver.com");
        User user2 = getUser("username2", "nickname2", "test2@naver.com");

        ChatRoomRequest chatRoomRequest = getChatRoomRequest(user2.getId());

        //when
        Long savedChatRoomId = chatService.saveChatRoom(user1, chatRoomRequest);
        Long chatId = chatService.findJoinedChatPK(user1, chatRoomRequest);

        //then
        assertThat(chatId).isEqualTo(savedChatRoomId);
    }

    @Test
    @DisplayName("존재하지 않은 톡방의 경우 -1을 반환해야 한다.")
    public void shouldReturnMinus1_whenChatExist(){
        //given
        User user1 = getUser("username1", "nickname1", "test1@naver.com");
        User user2 = getUser("username2", "nickname2", "test2@naver.com");

        ChatRoomRequest chatRoomRequest = getChatRoomRequest(user2.getId());

        //when
        Long chatId = chatService.findJoinedChatPK(user1, chatRoomRequest);

        //then
        assertThat(chatId).isEqualTo(-1);
    }
    
    @Test
    @DisplayName("1:N & TEACHER라면 기존 ChatRoom에 메시지를 작성할 수 있다")
    public void teacherCanWriteMesssage(){
        //given
        User user1 = getUser(Role.TEACHER, "username1", "nickname1", "test1@naver.com");
        User user2 = getUser("username2", "nickname2", "test2@naver.com");
        User user3 = getUser("username3", "nickname3", "test3@naver.com");

        ChatRoomRequest chatRoomRequest = getChatRoomRequest(user2.getId(), user3.getId());
        Long savedChatId = chatService.saveChatRoom(user1, chatRoomRequest);
        ChatRoom chatRoom = chatService.findChatRoomById(savedChatId);

        //when
        ChatMessageRequest messageRequest = ChatMessageRequest.builder().content("teacher's message").build();
        chatService.writeChatMessage(user1, savedChatId, messageRequest);

        //then
        assertThat(chatRoom.getChatUserMaps().size()).isEqualTo(3);
        assertThat(chatRoom.getChatMessages().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("ChatRoom을 ChatMessage가 포함된 ChatRoomResponse로 변환할 수 있다.")
    public void canConvertToResponseWithMessage(){
        //given
        User user1 = getUser("username1", "nickname1", "test1@naver.com");
        User user2 = getUser("username2", "nickname2", "test2@naver.com");

        ChatRoomRequest chatRoomRequest = getChatRoomRequest(user2.getId());
        Long savedChatRoomId = chatService.saveChatRoom(user1, chatRoomRequest);
        ChatRoom chatRoom = chatService.findChatRoomById(savedChatRoomId);

        //when
        ChatMessageRequest messageRequest1 = new ChatMessageRequest("message1");
        ChatMessageRequest messageRequest2 = new ChatMessageRequest("message2");

        chatService.writeChatMessage(user1, savedChatRoomId, messageRequest1);
        chatService.writeChatMessage(user2, savedChatRoomId, messageRequest2);

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        ChatRoomResponse roomResponse = chatService.convertToDetailChatResponse(user1, chatRoom, pageRequest);

        //then
        assertThat(roomResponse.getMessageList().getContent().size()).isEqualTo(3);
        assertThat(roomResponse.getIsDirect()).isEqualTo(1);
        assertThat(roomResponse.getId()).isEqualTo(savedChatRoomId);
    }

    @Test
    @DisplayName("ChatRoom을 ChatMessage가 포함되지 않은 ChatRoomResponse로 변환할 수 있다.")
    public void canConvertToResponseWithoutMessage(){
        //given
        User user1 = getUser("username1", "nickname1", "test1@naver.com");
        User user2 = getUser("username2", "nickname2", "test2@naver.com");

        ChatRoomRequest chatRoomRequest = getChatRoomRequest(user2.getId());
        Long savedChatRoomId = chatService.saveChatRoom(user1, chatRoomRequest);
        ChatRoom chatRoom = chatService.findChatRoomById(savedChatRoomId);

        //when
        ChatMessageRequest messageRequest1 = new ChatMessageRequest("message1");
        ChatMessageRequest messageRequest2 = new ChatMessageRequest("message2");

        chatService.writeChatMessage(user1, savedChatRoomId, messageRequest1);
        chatService.writeChatMessage(user2, savedChatRoomId, messageRequest2);

        ChatRoomResponse roomResponse = chatService.convertToChatRoomResponse(chatRoom);

        //then
        assertThat(roomResponse.getLastMessage()).isEqualTo(messageRequest2.getContent());
        assertThat(roomResponse.getIsDirect()).isEqualTo(1);
        assertThat(roomResponse.getId()).isEqualTo(savedChatRoomId);
        assertThat(roomResponse.getMessageList()).isNull();
    }

    @Test
    @DisplayName("chatRoomRequest의 content의 값이 null이면 예외가 발생한다.")
    public void throwException_whenContentIsNull(){
        //given
        User user1 = getUser("username1", "nickname1", "test1@naver.com");
        User user2 = getUser("username2", "nickname2", "test2@naver.com");

        List<Long> userId = new ArrayList<>();
        userId.add(user2.getId());

        ChatRoomRequest chatRoomRequest = ChatRoomRequest.builder()
                .receiverId(userId)
                .isDirect(1)
                .build();

        //when&then
        assertThatThrownBy(() -> chatService.saveChatRoom(user1, chatRoomRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.INVALID_PARAMETER.getMessage());
    }

    @Test
    @DisplayName("chatRoomRequest의 content의 값이 담겨져 있지 않으면 예외가 발생한다.")
    public void throwException_whenContentIsEmpty(){
        //given
        User user1 = getUser("username1", "nickname1", "test1@naver.com");
        User user2 = getUser("username2", "nickname2", "test2@naver.com");

        List<Long> userId = new ArrayList<>();
        userId.add(user2.getId());

        ChatRoomRequest chatRoomRequest = ChatRoomRequest.builder()
                .receiverId(userId)
                .content("")
                .isDirect(1)
                .build();

        //when&then
        assertThatThrownBy(() -> chatService.saveChatRoom(user1, chatRoomRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.INVALID_PARAMETER.getMessage());
    }

    @Test
    @DisplayName("최근에 작성한 메시지에 줄바꿈 문자가 있을 때, 응답 시 그대로 전달된다.")
    public void whenNextLineContains(){
        //given
        User user1 = getUser("username1", "nickname1", "test1@naver.com");
        User user2 = getUser("username2", "nickname2", "test2@naver.com");

        List<Long> userId = new ArrayList<>();
        userId.add(user2.getId());

        ChatRoomRequest chatRoomRequest = getChatRoomRequest(user2.getId());
        Long roomId = chatService.saveChatRoom(user1, chatRoomRequest);
        ChatRoom chatRoom = chatService.findChatRoomById(roomId);

        //when
        ChatMessageRequest request = ChatMessageRequest.builder().content("줄 바꿈 테스트\n 줄 바꿈 테스트").build();
        ChatMessageResponse chatMessageResponse = chatService.writeChatMessage(user1, roomId, request);

        //then
        assertThat(chatMessageResponse.getContent()).isEqualTo("줄 바꿈 테스트\n 줄 바꿈 테스트");
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
    private User getUser(Role role, String username, String nickname, String email) {
        User user = User.builder()
                .username(username)
                .nickname(nickname)
                .email(email)
                .role(role.getKey())
                .studentInfo(new StudentInfo("1", "2", "3"))
                .build();
        return userRepository.save(user);
    }
    private ChatRoomRequest getChatRoomRequest(Long userIds) {
        List<Long> ids = new ArrayList<>();
        ids.add(userIds);
        return ChatRoomRequest.builder()
                .receiverId(ids)
                .content("first direct message")
                .isDirect(1)
                .build();
    }
    private ChatRoomRequest getChatRoomRequest(Long... userIds) {
        List<Long> ids = new ArrayList<>();
        for (Long id : userIds) {
            ids.add(id);
        }
        return ChatRoomRequest.builder()
                .receiverId(ids)
                .content("first direct message")
                .isDirect(0)
                .build();
    }
    private ChatRoom saveAndGetChatRoom(User user1, User user2){
        ChatRoomRequest chatRoomRequest = getChatRoomRequest(user2.getId());
        Long saveChatRoomId = chatService.saveChatRoom(user1, chatRoomRequest);
        return chatService.findChatRoomById(saveChatRoomId);
    }
}