package Project.SangCom.security.controller;

import Project.SangCom.security.dto.OAuthRegisterRequest;
import Project.SangCom.security.dto.TokenRequest;
import Project.SangCom.security.service.JwtTokenProvider;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.dto.UserLoginResponse;
import Project.SangCom.user.service.UserService;
import Project.SangCom.util.exception.ExMessage;
import Project.SangCom.util.response.dto.CommonResponse;
import Project.SangCom.util.response.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    /**
     * FE에서 사용자에게서 받은 정보를 바탕으로 UserService에서 회원가입 진행
     */
    @PostMapping("/api/auth/register")
    public ResponseEntity<CommonResponse> register(HttpServletResponse response,
                                                   @RequestBody OAuthRegisterRequest registerRequest) throws IOException {

        // DTO -> Entity 변환
        User receivedUser = registerRequest.toEntity();

        Long registeredId = userService.registerUser(receivedUser);

        return ResponseEntity.ok().body(new CommonResponse(0, "회원가입 성공"));
    }

    @PostMapping("/api/auth/token")
    public ResponseEntity<CommonResponse> generateToken(HttpServletResponse response, @RequestBody TokenRequest email){

        // JWT access-token을 생성해서 header에 설정하고, refresh-token은 httpOnly cookie로 설정
        log.info(email.getEmail());
        Optional<User> userByEmail = userService.findUserByEmail(email.getEmail());

        if (userByEmail.isEmpty())
            log.info(ExMessage.DATA_ERROR_NOT_FOUND.getMessage());

        String accessToken = tokenProvider.createAccessToken(userByEmail.get());

        log.info(accessToken);
        response.setHeader("Authorization", accessToken);


        String refreshToken = tokenProvider.createRefreshToken(userByEmail.get());
        tokenProvider.setHttpOnlyCookie(response, refreshToken);


        return ResponseEntity.ok().body(new CommonResponse(0, ""));
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
