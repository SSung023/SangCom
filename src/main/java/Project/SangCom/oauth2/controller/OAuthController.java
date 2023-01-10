package Project.SangCom.oauth2.controller;

import Project.SangCom.oauth2.dto.OAuthRegisterRequest;
import Project.SangCom.user.dto.UserLoginResponse;
import Project.SangCom.util.response.dto.SingleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class OAuthController {

    /**
     * test 코드
     */
//    @GetMapping("/api/register")
//    public ResponseEntity<SingleResponse<SocialLoginResponse>> passUserEmailInfo(){
//
//        SocialLoginResponse socialLoginResponse = new SocialLoginResponse("test@naver.com");
//
//        return ResponseEntity.ok()
//                .body(new SingleResponse<>(0, null, socialLoginResponse));
//    }

    /**
     * FE에서 회원가입
     */
    @PostMapping("/api/register")
    public ResponseEntity<SingleResponse<UserLoginResponse>> register(@ModelAttribute OAuthRegisterRequest registerRequest){


        return ResponseEntity.ok()
                .body(new SingleResponse<>(0, null, ));
    }
}
