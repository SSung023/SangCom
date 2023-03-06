package Project.SangCom.chat.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Slice;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@NoArgsConstructor
public class ChatRoomResponse {

    private Long id; // 채팅방의 PK
    private int isDirect; // 1:1 대화인지 여부
    private String lastMessage; // 채팅방에서 나눈 마지막 대화의 내용
    private List<ChatUserDTO> userInfo = new ArrayList<>(); // DM에서 보여질 사용자의 정보
    private Slice<ChatMessageResponse> messageList; // 채팅 내용


    @Builder
    public ChatRoomResponse(Long id, int isDirect, String lastMessage,
                            List<ChatUserDTO> userInfo, Slice<ChatMessageResponse> messageList) {
        this.id = id;
        this.isDirect = isDirect;
        this.lastMessage = lastMessage;
        this.userInfo = userInfo;
        this.messageList = messageList;
    }
}
