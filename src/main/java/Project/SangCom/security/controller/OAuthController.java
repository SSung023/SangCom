package Project.SangCom.security.controller;

import Project.SangCom.security.dto.OAuthRegisterRequest;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.dto.UserLoginResponse;
import Project.SangCom.util.response.dto.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class OAuthController {

    /**
     * FE에서 회원가입
     */
    @PostMapping("/api/register")
    public ResponseEntity<SingleResponse<UserLoginResponse>> register(@ModelAttribute OAuthRegisterRequest registerRequest){


        return ResponseEntity.ok()
                .body(new SingleResponse<>(0, null, new UserLoginResponse()));
    }

    @GetMapping("/api/auth/login")
    public ResponseEntity<SingleResponse<UserLoginResponse>> response(){
        log.info("api login test");
        UserLoginResponse loginResponse = UserLoginResponse.builder()
                .email("test@naver.com")
                .role(Role.STUDENT)
                .nickname("nickname")
                .username("username")
                .build();

        return ResponseEntity.ok()
                .body(new SingleResponse<>(0, null, loginResponse));
    }

    @GetMapping("/api/authentication")
    public String testtest(){
        return "TESTETS";
    }

    @GetMapping("/api/test")
    public String test(){
        return "test";
    }
}
