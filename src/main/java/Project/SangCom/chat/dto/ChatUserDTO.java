package Project.SangCom.chat.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class ChatUserDTO {
    private Long userId;
    private String displayName;


    @Builder
    public ChatUserDTO(Long userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
    }
}
