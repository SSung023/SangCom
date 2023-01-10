package Project.SangCom.oauth2.handler;

import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.dto.UserLoginResponse;
import Project.SangCom.user.service.UserService;
import Project.SangCom.util.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService service;

    /**
     * 주의) kakao 소셜로그인 기준으로 적용됨!!!
     * OAuth2(소셜 로그인) 인증 완료 시 loadUser() -> successHandler로 이동
     * 1. 이메일을 통해 DB에서 사용자 정보를 찾음
     * 2-1. ROLE이 NOT_VERIFIED가 아니라면, 가입을 완료한 회원이므로 메인화면으로 리다이렉트
     * 2-2. ROLE이 NOT_VERIDIED라면, 가입을 진행해야하므로 회원가입 화면으로 리다이렉트
     *
     * flow test : loadUser() -> successHandler -> controller
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");

        String email = (String) attributes.get("email");
        Optional<User> optionalUser = service.findUserByEmail(email);

        if (!optionalUser.isPresent()) {
            throw new BusinessException("잘못된 접근입니다.");
        }

        // DB에서 사용자 정보를 찾고, Role 정보 저장
        User foundUser = optionalUser.get();
        Role role = foundUser.getRole();

        /**
         * Role.NOT_VERIFIED인 경우 = 회원가입이 필요한 회원인 경우
         * -> 회원가입 화면으로 리다이렉트
         */
        if (role.equals(Role.NOT_VERIFIED)){
            String redirect_url = "http://localhost:3000/register?email=" + foundUser.getEmail();
            getRedirectStrategy().sendRedirect(request, response, redirect_url);
            return;
        }

        /**
         * Role.NOT_VERIFIED가 아닌 경우 = 이미 가입한 회원인 경우
         * -> JWT 토큰 발급 후, 메인화면으로 리다이렉트
         */
        // DB에서 가져온 정보를 통해 FE에 보낼 정보를 담을 객체 생성
         UserLoginResponse userLoginResponse = UserLoginResponse.builder()
                .username(foundUser.getUsername())
                .nickname(foundUser.getNickname())
                .email(email)
                .role(foundUser.getRole())
                .build();

        // userLoginResponse의 정보를 바탕으로 JWT 토큰을 생성
        // jwtUtil.createToken() 등으로 JWT 토큰 생성

        // JWT토큰이 담긴 url 생성 후, 메인화면으로 리다이렉트
        String redirect_url = createRedirectTokenUrl();
        getRedirectStrategy().sendRedirect(request, response, redirect_url);

    }

    private String createRedirectTokenUrl(String token){
        return "localhost:3000/main";
    }

}
