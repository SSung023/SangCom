package Project.SangCom.oauth2.dto;

import Project.SangCom.user.domain.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OAuthRegisterRequest {

    private String username;
    private String nickname;
    private String role;

}
