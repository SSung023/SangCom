package Project.SangCom.oauth2.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class OAuthController {

    @RequestMapping("/oauth2/code/kakao")
    public String getCode(@RequestParam String code){
        log.info("Authorization code: " + code);
        return code;
    }
}
