package Project.SangCom.oauth2.security;

import Project.SangCom.oauth2.security.KakaoOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Order(1)
@RequiredArgsConstructor
public class SecurityConfig {

    private final KakaoOAuth2UserService kakaoOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain1(HttpSecurity http) throws Exception {
//        http.oauth2Login().userInfoEndpoint().userService(kakaoOAuth2UserService);

        // oauth2Login(): 기본 로그인 페이지 제공
        http.authorizeRequests()
                .antMatchers("/oauth2/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login();

        return http.build();
    }
}
