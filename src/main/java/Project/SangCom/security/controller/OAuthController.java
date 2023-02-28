package Project.SangCom.security.controller;

import Project.SangCom.security.dto.OAuthRegisterRequest;
import Project.SangCom.security.dto.TokenRequest;
import Project.SangCom.security.service.JwtTokenProviderService;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.domain.embedded.StudentInfo;
import Project.SangCom.user.dto.UserLoginResponse;
import Project.SangCom.user.service.UserService;
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ErrorCode;
import Project.SangCom.util.exception.SuccessCode;
import Project.SangCom.util.response.dto.CommonResponse;
import Project.SangCom.util.response.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
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
    public ResponseEntity<CommonResponse> register(@RequestBody OAuthRegisterRequest registerRequest) {

        // DTO -> Entity 변환
        User receivedUser = registerRequest.toEntity();

        Long registeredId = userService.registerUser(receivedUser);

        return ResponseEntity.ok().body(new CommonResponse(SuccessCode.CREATED.getStatus(), SuccessCode.CREATED.getMessage()));
    }

    /**
     * email을 통해 JWT 발급 대상자가 누구인지 확인하고 JWT(access, refresh)토큰을 발급한 뒤, header에 설정
     * @param email JWT token 발급 대상자의 이메일
     * @return
     */
    @PostMapping("/api/auth/token")
    public ResponseEntity<CommonResponse> generateToken(HttpServletResponse response, @RequestBody TokenRequest email){

        // JWT access-token을 생성해서 header에 설정하고, refresh-token은 httpOnly cookie로 설정
        User userByEmail = userService.findUserByEmail(email.getEmail());

        String accessToken = tokenService.setAccessToken(response, userByEmail);
        String refreshToken = tokenService.setRefreshToken(response, userByEmail);

        return ResponseEntity.ok().body(new CommonResponse(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage()));
    }

    /**
     * 1. request의 header에서 access-token 추출
     * 2. access-token 유효성 검증
     * 3. access-token으로부터 정보 요청 대상자가 누구인지 확인
     * 4. 정보 요청 대상자의 정보들을 담아서 response body에 담아서 전달
     */
    @GetMapping("/api/auth/user")
    public ResponseEntity<SingleResponse<UserLoginResponse>> sendUserInfo(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = tokenService.resolveAccessToken(request);

        if (!tokenService.validateAndReissueToken(request, response, accessToken)) {
            throw new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND);
        }

        UserLoginResponse loginResponse = tokenService.getRequestUserInfo(accessToken);

        return ResponseEntity.ok().body
                (new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), loginResponse));
    }

    /**
     * FE 메인화면에서 로그아웃 버튼 클릭 시 작동
     * BE: Cookie의 refreshToken 제거 & SecurityContext 사용자 삭제
     * FE: localStorage에 있는 accessToken 제거
     */
    @PostMapping("/api/auth/logout")
    public ResponseEntity<CommonResponse> userLogout(HttpServletResponse response) {

        // Cookie에 있는 refresh-token 제거
        tokenService.logout(response);
//        Cookie cookie = new Cookie("refreshToken", null);
//        cookie.setMaxAge(0);
//        cookie.setPath("/");
//        response.addCookie(cookie);


        return ResponseEntity.ok().body
                (new CommonResponse(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage()));
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

        return ResponseEntity.ok().body
                (new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), loginResponse));
    }

    @GetMapping("/api/auth/test/user")
    public ResponseEntity<CommonResponse> test(){
        log.info("FE 테스트를 위해 DB에 더미 유저 정보를 추가합니다.");
        User testUser = User.builder()
                .email("326ekdms@naver.com")
//                .email("adrians023@naver.com")
                .username("김댠")
                .nickname("단두대")
                .role(Role.STUDENT)
                .studentInfo(new StudentInfo("1", "2", "23"))
                .build();

        userService.registerUser(testUser);

        return ResponseEntity.ok(new CommonResponse(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage()));
    }

}
