package Project.SangCom.security.config;

import Project.SangCom.security.config.filter.JwtAuthenticationFilter;
import Project.SangCom.security.handler.OAuth2SuccessHandler;
import Project.SangCom.security.service.CustomOAuthService;
import Project.SangCom.security.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Order(1)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuthService customOAuthService;
    private final OAuth2SuccessHandler successHandler;
    private final JwtTokenProvider provider;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain1(HttpSecurity http) throws Exception {

        // REST api는 stateless하기 때문에 csrf disable
        http.cors().disable().csrf().disable()
                // 인증 없이 접근 가능한 uri 설정
                .authorizeRequests()
                .antMatchers("/api/test","/swagger-ui.html", "/auth/**", "/register/**").permitAll()
                .anyRequest().authenticated()

                // 오류로 인해 잠시 주석 처리
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(provider), UsernamePasswordAuthenticationFilter.class)

                // JWT 사용으로 인한 세션 미사용
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
