package Project.SangCom.user.dto;

import Project.SangCom.user.domain.Role;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserLoginResponse {

    private int code; // 0 성공, 1 실패

    private String username;
    private String nickname;
    private String email;
    private Role role;

    @Builder
    public UserLoginResponse(int code, String username, String nickname, String email, Role role) {
        this.code = code;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
    }
}
