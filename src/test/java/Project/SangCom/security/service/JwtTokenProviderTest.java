package Project.SangCom.security.service;

import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;


@SpringBootTest
@Slf4j
@ActiveProfiles({"jwt"})
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider provider;
    @Autowired
    WebApplicationContext context;
    MockMvc mockMvc;

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access-token-validity-in-seconds}")
    private Long accessTokenValidityInMilliseconds;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenValidityInMilliseconds;


    @BeforeEach
    public void setup(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }



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
        User user = getUser();

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
        User user = getUser();

        //when
        String accessToken = provider.createAccessToken(user);

        //then
        log.info(accessToken);
    }

    @Test
    @DisplayName("JWT refresh token을 생성")
    public void generateJwtRefreshToken(){
        //given
        User user = getUser();

        //when
        String refreshToken = provider.createRefreshToken(user);

        //then
        log.info(refreshToken);
    }

    @Test
    @DisplayName("access-token인 경우 토큰 추출 과정에서 prefix(Bearer)를 제거해야 한다.")
    public void extractAccessShouldRemovePrefix(){
        //given
        User user = getUser();
        
        //when
        String accessToken = provider.createAccessToken(user);

        //then
        Assertions.assertThat
                (provider.resolveTokenFromString("Bearer " + accessToken)).isEqualTo(accessToken);
    }
    
    @Test
    @DisplayName("refresh-token인 경우 토큰 추출 과정에서 그대로 반환해야 한다.")
    public void extractRefreshShouldExtractLiterally(){
        //given
        User user = getUser();
        
        //when
        String refreshToken = provider.createRefreshToken(user);
        
        //then
        Assertions.assertThat(provider.resolveTokenFromString(refreshToken)).isEqualTo(refreshToken);
    }


    @Test
    @DisplayName("JWT access token에서 JWT 토큰 정보(Claim)를 꺼내올 수 있다.")
    public void checkClaimsOfAccessToken(){
        //given
        User user = getUser();

        //when
        String accessToken = provider.createAccessToken(user);
        Claims claims = provider.parseClaims(accessToken);

        //then
        log.info(claims.toString());
        Assertions.assertThat(claims.get("email")).isEqualTo("test@naver.com");
        Assertions.assertThat(claims.get("sub")).isEqualTo("test@naver.com");
        Assertions.assertThat(claims.get("role")).isEqualTo("ROLE_STUDENT");
    }
    
    @Test
    @DisplayName("JWT access token이 유효한지 provider를 통해 확인할 수 있다.")
    public void checkValidationOfAccessToken(){
        //given
        User user = getUser();
        
        //when
        String accessToken = provider.createAccessToken(user);

        //then
        Assertions.assertThat(provider.validateToken(accessToken)).isTrue();
    }

    @Test
    @DisplayName("JWT token에서 회원 정보(이메일)를 추출할 수 있다.")
    public void getUserPkFromToken(){
        //given
        User user = getUser();

        //when
        String accessToken = provider.createAccessToken(user);
        String subject = provider.getUserPk(accessToken);
        
        //then
        Assertions.assertThat(subject).isEqualTo("test@naver.com");
    }



    private User getUser() {
        User user = User.builder()
                .username("username")
                .nickname("nickname")
                .email("test@naver.com")
                .role(Role.STUDENT)
                .build();
        return user;
    }


}