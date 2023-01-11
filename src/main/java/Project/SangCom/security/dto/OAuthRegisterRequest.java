package Project.SangCom.security.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OAuthRegisterRequest {

    private String username;
    private String nickname;
    private String role;

}
