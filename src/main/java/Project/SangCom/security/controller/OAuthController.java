package Project.SangCom.security.controller;

import Project.SangCom.security.dto.OAuthRegisterRequest;
import Project.SangCom.security.dto.TokenRequest;
import Project.SangCom.security.service.JwtTokenProviderService;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.dto.UserLoginResponse;
import Project.SangCom.user.service.UserService;
import Project.SangCom.util.exception.BusinessException;
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
    private final JwtTokenProviderService tokenService;

    /**
     * FE에서 사용자에게서 받은 정보를 바탕으로 UserService에서 회원가입 진행
     */
    @PostMapping("/api/auth/register")
    public ResponseEntity<CommonResponse> register(@RequestBody OAuthRegisterRequest registerRequest) throws IOException {

        // DTO -> Entity 변환
        User receivedUser = registerRequest.toEntity();

        Long registeredId = userService.registerUser(receivedUser);

        return ResponseEntity.ok().body(new CommonResponse(0, "회원가입 성공"));
    }

    /**
     * email을 통해 JWT 발급 대상자가 누구인지 확인하고 JWT(access, refresh)토큰을 발급한 뒤, header에 설정
     * @param email JWT token 발급 대상자의 이메일
     * @return
     */
    @PostMapping("/api/auth/token")
    public ResponseEntity<CommonResponse> generateToken(HttpServletResponse response, @RequestBody TokenRequest email){

        // JWT access-token을 생성해서 header에 설정하고, refresh-token은 httpOnly cookie로 설정
        Optional<User> userByEmail = userService.findUserByEmail(email.getEmail());
        if (userByEmail.isEmpty())
            log.info(ExMessage.DATA_ERROR_NOT_FOUND.getMessage());

        String accessToken = tokenService.setAccessToken(response, userByEmail.get());
        String refreshToken = tokenService.setRefreshToken(response, userByEmail.get());

        return ResponseEntity.ok().body(new CommonResponse(0, ""));
    }

    /**
     * 1. request의 header에서 access-token 추출
     * 2. access-token 유효성 검증
     * 3. access-token으로부터 정보 요청 대상자가 누구인지 확인
     * 4. 정보 요청 대상자의 정보들을 담아서 response body에 담아서 전달
     */
    @GetMapping("/api/auth/user")
    public ResponseEntity<SingleResponse<UserLoginResponse>> sendUserInfo(HttpServletRequest request, HttpServletResponse response){
        String accessToken = tokenService.resolveAccessToken(request);

        if (!tokenService.validateAndReissueToken(request, response, accessToken)) {
            throw new BusinessException(ExMessage.DATA_ERROR_NOT_FOUND);
        }

        UserLoginResponse loginResponse = tokenService.getRequestUserInfo(accessToken);

        return ResponseEntity.ok().body(new SingleResponse<>(0, "", loginResponse));
    }






    // test code
    @GetMapping("/api/auth/test/login")
    public ResponseEntity<SingleResponse<UserLoginResponse>> loginTest(){
        log.info("api login test");
        UserLoginResponse loginResponse = UserLoginResponse.builder()
                .role(Role.STUDENT)
                .nickname("nickname")
                .username("username")
                .build();

        return ResponseEntity.ok()
                .body(new SingleResponse<>(0, null, loginResponse));
    }

}
