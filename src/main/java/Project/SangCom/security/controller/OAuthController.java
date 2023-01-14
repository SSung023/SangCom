package Project.SangCom.security.controller;

import Project.SangCom.security.dto.OAuthRegisterRequest;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.dto.UserLoginResponse;
import Project.SangCom.user.service.UserService;
import Project.SangCom.util.response.dto.CommonResponse;
import Project.SangCom.util.response.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final UserService userService;

    /**
     * FE에서 사용자에게서 받은 정보를 바탕으로 UserService에서 회원가입 진행
     */
    @PostMapping("/api/auth/register")
    public ResponseEntity<CommonResponse> register(@RequestBody OAuthRegisterRequest registerRequest){
        log.info(registerRequest.toString());

        Long registeredId = userService.registerUser(registerRequest);

        log.info(userService.findUserById(registeredId).toString());


        return ResponseEntity.ok().body(new CommonResponse(0, "회원가입 성공"));
    }

    // test code
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

}
