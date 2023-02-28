package Project.SangCom.chat.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@NoArgsConstructor
public class ChatRoomRequest {

    private String content; // 전송하는 첫 메시지
    private List<Long> receiverId; // 메시지를 받는 수신자들의 id(PK)
    private int isDirect; // 1:1인지 여부 - 1:true, 0:false


    @Builder
    public ChatRoomRequest(String content, List<Long> receiverId, int isDirect) {
        this.content = content;
        this.receiverId = receiverId;
        this.isDirect = isDirect;
    }
}
