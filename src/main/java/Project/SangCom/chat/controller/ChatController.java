package Project.SangCom.chat.controller;

import Project.SangCom.chat.domain.ChatRoom;
import Project.SangCom.chat.dto.ChatMessageRequest;
import Project.SangCom.chat.dto.ChatMessageResponse;
import Project.SangCom.chat.dto.ChatRoomRequest;
import Project.SangCom.chat.dto.ChatRoomResponse;
import Project.SangCom.chat.service.ChatService;
import Project.SangCom.user.domain.User;
import Project.SangCom.util.exception.SuccessCode;
import Project.SangCom.util.response.dto.ListResponse;
import Project.SangCom.util.response.dto.PagingResponse;
import Project.SangCom.util.response.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ChatController {
    private final ChatService chatService;
    private final int PAGE_SIZE = 10;



    /**
     * 새로운 ChatRoom 생성 요청
     * @param chatRoomRequest ChatRoom 생성에 필요한 정보들
     */
    @PostMapping("/message")
    public ResponseEntity<SingleResponse<ChatRoomResponse>> createNewChatRoom(@RequestBody ChatRoomRequest chatRoomRequest){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long joinedChat = chatService.findJoinedChatPK(user, chatRoomRequest);
        if (joinedChat == -1){ // 기존에 있던 채팅방이 없는 경우
            joinedChat = chatService.saveChatRoom(user, chatRoomRequest);
        }
        else { // 기존에 있던 채팅방이 있는 경우 - 메시지 추가 요청 필요
            chatService.writeChatMessage(user, joinedChat, new ChatMessageRequest(chatRoomRequest.getContent()));
        }

        ChatRoomResponse chatRoomResponse = chatService.convertToChatRoomResponse(chatService.findChatRoomById(joinedChat));

        return ResponseEntity.ok()
                .body(new SingleResponse<>(SuccessCode.CREATED.getStatus(), SuccessCode.CREATED.getMessage(), chatRoomResponse));
    }


    /**
     * 자신이 속해있는 모든 톡방들을 검색하여 반환 - 톡방 미리보기 용으로 반환
     */
    @GetMapping("/message/list")
    public ResponseEntity<ListResponse<ChatRoomResponse>> getChatList
        (@PageableDefault(size = PAGE_SIZE, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ChatRoomResponse> chatRoomList = chatService.getJoinedChatRoomList(user);

        return ResponseEntity.ok()
                .body(new ListResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), chatRoomList));
    }

    /**
     * 특정 채팅방에서 주고 받은 메시지 리스트를 반환
     * @param chatRoomId 메시지 리스트를 얻고 싶은 ChatRoom PK
     */
    @GetMapping("/message/{chatRoomId}")
    public ResponseEntity<PagingResponse<ChatMessageResponse>> getChatMessages
        (@PageableDefault(size = PAGE_SIZE, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
         @PathVariable Long chatRoomId){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Slice<ChatMessageResponse> messageList = chatService.getChatMessageList(user, chatRoomId, pageable);

        return ResponseEntity.ok()
                .body(new PagingResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.CREATED.getMessage(), messageList));
    }

    /**
     * 특정 채팅방에서 메시지 등록 요청
     * @param chatRoomId 메시지를 등록하고 싶은 ChatRoom PK
     */
    @PostMapping("/message/{chatRoomId}")
    public ResponseEntity<SingleResponse<ChatMessageResponse>> sendMessage(@PathVariable Long chatRoomId, @RequestBody ChatMessageRequest chatMessageRequest){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ChatMessageResponse chatMessageResponse = chatService.writeChatMessage(user, chatRoomId, chatMessageRequest);

        return ResponseEntity.ok()
                .body(new SingleResponse<>(SuccessCode.CREATED.getStatus(), SuccessCode.CREATED.getMessage(), chatMessageResponse));
    }

    /**
     * 주어진 사용자들이 모두 참여하고 있는 ChatRoom이 있는지 여부 확인 후,
     * 있으면 ChatRoomResponse 전달
     * 없으면 ChatRoomResponse에 모두 null 넣어서 전달
     * @param targetId ChatRoom이 있는지 확인할 대상
     */
    @GetMapping("/message")
    public ResponseEntity<SingleResponse<ChatRoomResponse>> getChatRoom
        (@PageableDefault(size = PAGE_SIZE, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
         @RequestParam Long targetId){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Long> userIds = new ArrayList<>();
        userIds.add(user.getId());
        userIds.add(targetId);

        ChatRoomResponse roomResponse = chatService.findJoinedChat(user, userIds, pageable);

        return ResponseEntity.ok()
                .body(new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), roomResponse));
    }
}
