package Project.SangCom.security.config;

import Project.SangCom.security.config.filter.JwtAuthenticationFilter;
import Project.SangCom.security.handler.OAuth2SuccessHandler;
import Project.SangCom.security.service.CustomOAuthService;
import Project.SangCom.security.service.JwtTokenProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@Order(1)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuthService customOAuthService;
    private final JwtTokenProviderService providerService;
    private final OAuth2SuccessHandler successHandler;
    public static final String permitURI[] = {"/api/auth/**", "/swagger-ui.html", "/swagger-ui/**"
            ,"/v3/api-docs/**", "/v3/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**"};

    @Bean
    public SecurityFilterChain filterChain1(HttpSecurity http) throws Exception {

        // REST api는 stateless하기 때문에 csrf disable
        http.csrf().disable()
                .httpBasic().disable().formLogin().disable()
                .anonymous().and()
                // 인증 없이 접근 가능한 uri 설정 & CORS/preflight 설정
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers(permitURI).permitAll()
                .antMatchers("/api/like/**").hasAnyRole("STUDENT", "STUDENT_COUNCIL", "TEACHER", "ADMIN")
                .antMatchers("/api/board/free/**").hasAnyRole("STUDENT", "STUDENT_COUNCIL", "ADMIN")
                .anyRequest().authenticated()

                // JWT 검증 필터 추가
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(providerService), UsernamePasswordAuthenticationFilter.class)

                // JWT 사용으로 인한 세션 미사용
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                // OAuth 로그인 설정
                .oauth2Login()
                .successHandler(successHandler)
                .authorizationEndpoint()

                // 로그인이 성공하면 해당 유저정보를 들고 customOAuthService에서 후 처리 진행
                .and()
                .userInfoEndpoint().userService(customOAuthService);

        return http.build();
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource(){
//        CorsConfiguration configuration = new CorsConfiguration();
//    }
}
