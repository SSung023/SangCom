package Project.SangCom.oauth2.security;

import Project.SangCom.oauth2.service.CustomOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Order(1)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuthService customOAuthService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain1(HttpSecurity http) throws Exception {
        /**
         * .antMathers(): 해당 uri는 접근 허용
         * .anyRequest().authenticated(): 외에는 모두 인증 필요
         * .oauth2Login(): OAuth 로그인 설정
         * .defaultSuccessURl(): 로그인 성공 시 이동할 url
         * .userInfoEndpoint().userService(): 로그인이 성공하면 해당 유저정보를 들고 oAuthService에서 후처리 진행
         */
        // SuccessHandler 등 핸들러 클래스 추가 필요
        http.authorizeRequests()
                .antMatchers("/login/**", "/swagger-ui.html").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .defaultSuccessUrl("/register")
                .authorizationEndpoint()
                .baseUri("/oauth2/authorization")
                .and()
                .userInfoEndpoint().userService(customOAuthService);

        return http.build();
    }
}
