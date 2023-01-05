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
     *
     * @return
     */
    @GetMapping("/api/register")
    public ResponseEntity<SingleResponse<SocialLoginResponse>> passUserEmailInfo(){

        SocialLoginResponse socialLoginResponse = new SocialLoginResponse("test@naver.com");

        return ResponseEntity.ok()
                .body(new SingleResponse<>(0, null, socialLoginResponse));
    }
}
