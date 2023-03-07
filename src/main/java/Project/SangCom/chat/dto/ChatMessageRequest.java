package Project.SangCom.chat.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class ChatMessageRequest {

    private String content; // 메시지의 내용

    @Builder
    public ChatMessageRequest(String content) {
        this.content = content;
    }
}
