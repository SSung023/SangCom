package Project.SangCom.oauth2.security;

import Project.SangCom.oauth2.service.KakaoOAuth2UserService;
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
                .antMatchers("/oauth2/**").permitAll()
//                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .anyRequest().authenticated()
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .and()
                .oauth2Login() // 기본 로그인 페이지 제공
                .defaultSuccessUrl("/");

        return http.build();
    }
}
