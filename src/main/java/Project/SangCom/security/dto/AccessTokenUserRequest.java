package Project.SangCom.security.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * access-token 생성 시 필요한 데이터들을 담는 DTO 객체
 */
@Data
@NoArgsConstructor
public class AccessTokenUserRequest {
    private String email; // ex) test@gmail.com
    private String role; // ex) ROLE_STUDENT


    @Builder
    public AccessTokenUserRequest(String email, String role) {
        this.email = email;
        this.role = role;
    }
}
