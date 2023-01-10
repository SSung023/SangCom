package Project.SangCom.oauth2.security;

import Project.SangCom.oauth2.handler.OAuth2SuccessHandler;
import Project.SangCom.oauth2.service.CustomOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Order(1)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuthService customOAuthService;
    private final OAuth2SuccessHandler successHandler;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain1(HttpSecurity http) throws Exception {
        // 인증 없이 접근 가능한 uri 설정
        http.authorizeRequests()
                .antMatchers("/login/**", "/swagger-ui.html").permitAll()
                .anyRequest().authenticated()

                // JWT로 인한 세션 설정 미사용
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // OAuth 로그인 설정
                .and()
                .oauth2Login()
                .successHandler(successHandler)
                .authorizationEndpoint()

                // 로그인이 성공하면 해당 유저정보를 들고 customOAuthService에서 후처리 진행
                .and()
                .userInfoEndpoint().userService(customOAuthService);

        return http.build();
    }
}
