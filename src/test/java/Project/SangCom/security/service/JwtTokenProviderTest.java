package Project.SangCom.security.service;

import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@SpringBootTest
@Slf4j
@ActiveProfiles({"jwt"})
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider provider;
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access-token-validity-in-seconds}")
    private Long accessTokenValidityInMilliseconds;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenValidityInMilliseconds;



    @Test
    @DisplayName("JWT 토큰의 header 생성")
    public void createJwtHeader(){
        Map<String, Object> header = provider.createHeader();

        Assertions.assertThat(header.get("typ")).isEqualTo("JWT");
        Assertions.assertThat(header.get("alg")).isEqualTo("HS512");
    }

    @Test
    @DisplayName("JWT 토큰의 claim 생성")
    public void createJwtClaims(){
        // given
        User user = User.builder()
                .username("username")
                .nickname("nickname")
                .email("test@naver.com")
                .role(Role.STUDENT)
                .build();

        // when
        Map<String, Object> claims = provider.createClaims(user);

        // then
        Assertions.assertThat(claims.get("email")).isEqualTo("test@naver.com");
        Assertions.assertThat(claims.get("role")).isEqualTo("ROLE_STUDENT");
    }


    @Test
    @DisplayName("email을 통해 사용자를 DB에서 찾아 JWT access token을 생성한다.")
    public void generateJwtAccessToken(){
        //given
        User user = User.builder()
                .username("username")
                .nickname("nickname")
                .email("test@naver.com")
                .role(Role.STUDENT)
                .build();

        //when

        String accessToken = provider.createAccessToken(user);

        //then
        log.info(accessToken);
    }

    @Test
    @DisplayName("JWT refresh token을 생성")
    public void generateJwtRefreshToken(){
        //given
        User user = User.builder()
                .username("username")
                .nickname("nickname")
                .email("test@naver.com")
                .role(Role.STUDENT)
                .build();

        //when
        String refreshToken = provider.createRefreshToken(user);

        //then
        log.info(refreshToken);
    }

}