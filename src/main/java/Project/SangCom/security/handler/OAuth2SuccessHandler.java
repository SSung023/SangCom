package Project.SangCom.security.handler;

import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.service.UserService;
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ExMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

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

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // SecurityContext의 Authentication에 저장되는 객체: OAuth2AuthenticationToken
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");

        String email = (String) attributes.get("email");
        Optional<User> userByEmail = userService.findUserByEmail(email);

        // 예외 처리
        if (userByEmail.isEmpty()) {
            log.error(String.valueOf(ExMessage.DATA_ERROR_NOT_FOUND));
            throw new BusinessException(ExMessage.DATA_ERROR_NOT_FOUND);
        }

        User user = userByEmail.get();
        Role role = user.getRole();

        /**
         * NOT_VERIFIED인 경우 회원가입 페이지로 리다이렉트
         */
        if (role.equals(Role.NOT_VERIFIED)) {
            log.info("ROLE이 NOT_VERIFIED(회원가입이 완료되지 않은 회원)이므로 회원가입 페이지로 리다이렉트 합니다.");

            String redirect_url = UriComponentsBuilder.fromUriString("http://localhost:3000/register")
                    .queryParam("email", email)
                    .build().toUriString();

            getRedirectStrategy().sendRedirect(request, response, redirect_url);
            return;
        }


        /**
         * 제대로 된 Role인 경우, /auth?email=""로 리다이렉트
         */
        log.info("ROLE이 NOT_VERIFIED가 아닌 경우 token 인가 페이지로 리다이렉트 합니다.");
        String redirect_url = UriComponentsBuilder.fromUriString("http://localhost:3000/auth")
                .queryParam("email", email)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirect_url);
    }

}
