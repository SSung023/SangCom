package Project.SangCom.oauth2.controller;

import Project.SangCom.oauth2.dto.SocialLoginResponse;
import Project.SangCom.util.response.dto.CommonResponse;
import Project.SangCom.util.response.dto.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Slf4j
public class OAuthController {

    /**
     * test 코드
     */
    @GetMapping("/api/register")
    public ResponseEntity<SingleResponse<SocialLoginResponse>> passUserEmailInfo(){

        SocialLoginResponse socialLoginResponse = new SocialLoginResponse("test@naver.com");

        return ResponseEntity.ok()
                .body(new SingleResponse<>(0, null, socialLoginResponse));
    }

    /**
     * 소셜 로그인 이후 /register로 요청이 들어오면 JWT 토큰을 발급하여 리액트 uri로 리다이렉트 실행
     * !! 현재는 JWT 토큰 발급 및 uri 설정 과정이 없음 추후 추가 예정 !!
     */
    @GetMapping("/register")
    public void passUserEmailInfo2(HttpServletResponse response) throws IOException {
        String redirect_uri="http://localhost:3000/register?email=test@naver.com";
        response.sendRedirect(redirect_uri);
    }
}
