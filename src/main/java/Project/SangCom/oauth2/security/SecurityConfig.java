package Project.SangCom.oauth2.security;

import Project.SangCom.oauth2.service.KakaoOAuth2UserService;
import Project.SangCom.oauth2.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Order(1)
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuthService oAuthService;
    private final KakaoOAuth2UserService kakaoOAuth2UserService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain1(HttpSecurity http) throws Exception {
        // 로그인이 성공하면 해당 유저정보를 들고 kakaoOAuth2UserService에서 후 처리 진행
//        http.oauth2Login().userInfoEndpoint().userService(kakaoOAuth2UserService);

        http.authorizeRequests()
                .antMatchers("/login/**", "/swagger-ui.html").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .defaultSuccessUrl("/register")
                .authorizationEndpoint()
                .baseUri("/oauth2/authorization")
                .and()
                .userInfoEndpoint().userService(kakaoOAuth2UserService);

        return http.build();
    }
}
