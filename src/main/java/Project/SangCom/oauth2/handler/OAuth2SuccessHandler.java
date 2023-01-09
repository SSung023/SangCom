package Project.SangCom.oauth2.handler;

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
     * 1. 이메일을 통해 가입 여부 확인
     * 2-1. DB에 있다면 가입이 완료되었다는 뜻이므로 JWT 토큰을 생성하고, 리다이렉트한다.
     * 2-2. DB에 없다면 가입을 해야한다는 뜻이므로, 회원가입페이지로 리다이렉트 ->
     *
     * test code : loadUser() -> successHandler -> controller
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//        Map<String, Object> attributes = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
//
//        String email = (String) attributes.get("email");
//        Optional<User> optionalUser = service.findUserByEmail(email);
//
//        UserLoginResponse userLoginResponse;
//
//        // 로직 수정 필요!!!
//        if (optionalUser.isPresent()) {
//            throw new BusinessException("잘못된 접근입니다.");
//        }
//        // DB에 일치하는 email이 있는 경우 = 회원가입이 완료된 사용자
//        else {
//            User foundUser = optionalUser.get();
//            userLoginResponse = UserLoginResponse.builder()
//                    .username(foundUser.getUsername())
//                    .nickname(foundUser.getNickname())
//                    .email(email)
//                    .role(foundUser.getRole())
//                    .build();
//        }
//
//        // userLoginResponse의 정보를 바탕으로 JWT 토큰을 생성
//
//
//
//        //
//
//        log.info("handler end");

        // for test
//        String redirect_uri="http://localhost:3000/register?email=test@naver.com";
//        response.sendRedirect(redirect_uri);
    }

    private String createRedirectUrl(String token){
        return "";
    }

}
