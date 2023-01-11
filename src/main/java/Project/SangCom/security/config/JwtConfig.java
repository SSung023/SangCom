package Project.SangCom.security.config;

import Project.SangCom.security.service.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access-token-validity-in-seconds}")
    private Long accessTokenValidTime;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenValidTime;

    @Bean
    public JwtTokenProvider jwtTokenProvider(){
        return new JwtTokenProvider
                (secretKey, accessTokenValidTime, refreshTokenValidTime);
    }
}
