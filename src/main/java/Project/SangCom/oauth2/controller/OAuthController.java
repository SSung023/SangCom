package Project.SangCom.oauth2.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Slf4j
public class OAuthController {

    /**
     * 카카오 로그인 성공 이후 /register uri로 이동
     * @return 웹 페이지에 띄울 성공 메세지(temp)
     * FE ->BE : localhost:8080/register
     *
     */
    @GetMapping("/register")
    public void test(HttpServletResponse response) throws IOException {
        String redirect_uri="http://localhost:3000/register";
        response.sendRedirect(redirect_uri);
    }


    @GetMapping("/api/test")
    public String test(){
        log.info("test 성공");
        return "test";
    }
}
