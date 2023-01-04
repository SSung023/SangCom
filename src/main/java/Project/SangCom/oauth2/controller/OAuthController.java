package Project.SangCom.oauth2.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class OAuthController {

    /**
     * 카카오 로그인 성공 이후 /register uri로 이동
     * @return 웹 페이지에 띄울 성공 메세지(temp)
     */
    @GetMapping("/register")
    public String test(){
        return "카카오 로그인 성공";
    }
}
