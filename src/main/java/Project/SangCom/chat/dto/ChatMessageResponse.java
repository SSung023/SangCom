package Project.SangCom.chat.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
@NoArgsConstructor
public class ChatMessageResponse {
    private Long id; // chatMessage의 PK
    private String content; // chatMessage 내용
    private String author; // 작성자 이름  ex) 홍길동 (1반)
    private int isOwner; // 사용자가 작성한 메세지인지 여부
    private LocalDateTime createdDate; // 메시지가 작성된 시간


    @Builder
    public ChatMessageResponse(Long id, String content, String author,
                               int isOwner, LocalDateTime createdDate) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.isOwner = isOwner;
        this.createdDate = createdDate;
    }
}
