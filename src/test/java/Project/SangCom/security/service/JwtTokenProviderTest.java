package Project.SangCom.security.service;

import Project.SangCom.security.dto.AccessTokenUserRequest;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.Map;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles({"jwt"})
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider provider;
    @Autowired
    private UserRepository repository;

    @Autowired
    WebApplicationContext context;
    MockMvc mockMvc;

    @Value("${jwt.refresh-secret}")
    private String refreshSecret;
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
        AccessTokenUserRequest userDTO = convertToUser(user);

        // when
        Map<String, Object> claims = provider.createClaims(userDTO);

        // then
        Assertions.assertThat(claims.get("email")).isEqualTo("test@naver.com");
        Assertions.assertThat(claims.get("role")).isEqualTo("ROLE_STUDENT");
    }

    @Test
    @DisplayName("email을 통해 사용자를 DB에서 찾아 JWT access token을 생성한다.")
    public void generateJwtAccessToken(){
        //given
        User user = getUser();
        AccessTokenUserRequest userDTO = convertToUser(user);

        //when
        String accessToken = provider.createAccessToken(userDTO);

        //then
        log.info(accessToken);
    }

    @Test
    @DisplayName("JWT refresh token을 생성")
    public void generateJwtRefreshToken(){
        //given
        User user = getUser();
        AccessTokenUserRequest userDTO = convertToUser(user);

        //when
        String refreshToken = provider.createRefreshToken(userDTO);

        //then
        log.info(refreshToken);
    }

    @Test
    @DisplayName("JWT refresh token의 유효성 확인")
    public void validateRefreshToken(){
        //given
        User user = getUser();
        AccessTokenUserRequest userDTO = convertToUser(user);

        //when
        String refreshToken = provider.createRefreshToken(userDTO);

        //then
        log.info(refreshToken);
        provider.validateRefreshToken(refreshToken);
    }

    @Test
    @DisplayName("access-token인 경우 토큰 추출 과정에서 prefix(Bearer)를 제거해야 한다.")
    public void extractAccessShouldRemovePrefix(){
        //given
        User user = getUser();
        AccessTokenUserRequest userDTO = convertToUser(user);
        
        //when
        String accessToken = provider.createAccessToken(userDTO);

        //then
        Assertions.assertThat
                (provider.resolveTokenFromString("Bearer " + accessToken)).isEqualTo(accessToken);
    }
    
    @Test
    @DisplayName("refresh-token인 경우 토큰 추출 과정에서 그대로 반환해야 한다.")
    public void extractRefreshShouldExtractLiterally(){
        //given
        User user = getUser();
        AccessTokenUserRequest userDTO = convertToUser(user);
        
        //when
        String refreshToken = provider.createRefreshToken(userDTO);
        
        //then
        Assertions.assertThat(provider.resolveTokenFromString(refreshToken)).isEqualTo(refreshToken);
    }


    @Test
    @DisplayName("JWT access token에서 JWT 토큰 정보(Claim)를 꺼내올 수 있다.")
    public void checkClaimsOfAccessToken(){
        //given
        User user = getUser();
        AccessTokenUserRequest userDTO = convertToUser(user);

        //when
        String accessToken = provider.createAccessToken(userDTO);
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
        AccessTokenUserRequest userDTO = convertToUser(user);
        
        //when
        String accessToken = provider.createAccessToken(userDTO);
        accessToken = provider.resolveTokenFromString(accessToken);

        //then
        Assertions.assertThat(provider.validateAccessToken(accessToken)).isTrue();
    }

    @Test
    @DisplayName("JWT token에서 회원 정보(이메일)를 추출할 수 있다.")
    public void getUserPkFromToken(){
        //given
        User user = getUser();
        AccessTokenUserRequest userDTO = convertToUser(user);

        //when
        String accessToken = provider.createAccessToken(userDTO);
        String subject = provider.getUserPk(accessToken);
        
        //then
        Assertions.assertThat(subject).isEqualTo("test@naver.com");
    }

    @Test
    @DisplayName("token의 subject를 통해 DB에서 Authentication 객체를 받아올 수 있다.")
    public void getAuthenticationByTokenSubject(){
        //given
        User user = getUser();
        AccessTokenUserRequest userDTO = convertToUser(user);
        repository.save(user);
        
        //when
        String accessToken = provider.createAccessToken(userDTO);
        Authentication authentication = provider.getAuthentication(accessToken);

        //then
        Assertions.assertThat(authentication).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        log.info(authentication.toString());
    }

    @Test
    @DisplayName("set-cookie header에서 refresh-token를 추출할 수 있다.")
    public void extractRefreshTokenFromHeader(){
        //given
        User user = getUser();
        AccessTokenUserRequest userDTO = convertToUser(user);
        MockHttpServletRequest request = new MockHttpServletRequest();

        String refreshToken = provider.createRefreshToken(userDTO);
        ResponseCookie cookie
                = ResponseCookie.from("refreshToken", refreshToken)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .maxAge(6 * 60 * 60) // 24hour * 60min * 60sec
                .build();


        //when
        request.addHeader("Set-Cookie", cookie.toString());
        String receivedRefresh = provider.resolveRefreshTokenFromHeader(request);

        // then
        Assertions.assertThat(receivedRefresh).isEqualTo(refreshToken);

    }

    @Test
    @DisplayName("refresh-token에서 사용자 식별 정보인 email을 알 수 있다.")
    public void getUserInfoByRefreshToken(){
        //given
        User user = getUser();
        AccessTokenUserRequest userDTO = convertToUser(user);

        //when
        String refreshToken = provider.createRefreshToken(userDTO);
        log.info(refreshToken);

        String userPk = provider.getUserPkByRefresh(refreshToken);

        //then
        Assertions.assertThat(user.getEmail()).isEqualTo(userPk);
    }

    @Test
    @DisplayName("refresh-token의 만료 시간이 1/2 이하가 아니라면 false를 반환한다.")
    public void checkRefreshExpiredTime(){
        //given
        User user = getUser();
        AccessTokenUserRequest userDTO = convertToUser(user);

        //when
        Long now = System.currentTimeMillis();

        String refreshToken = createTempRefreshToken(userDTO, now);

        Boolean isExpired = provider.checkRefreshExpirationTime(refreshToken);

        //then
        Assertions.assertThat(isExpired).isFalse();
    }

    @Test
    @DisplayName("refresh-token의 만료 시간이 1/2 이하라면 true를 반환한다.")
    public void checkRefreshExpiredTimeOver(){
        //given
        User user = getUser();
        AccessTokenUserRequest userDTO = convertToUser(user);

        //when
        Long now = System.currentTimeMillis();

        String refreshToken = createTempRefreshToken(userDTO, now);

        Claims claims = Jwts.parser().setSigningKey(refreshSecret).parseClaimsJws(refreshToken).getBody();

        Date expiration = claims.getExpiration();
        Date compareDate = new Date(System.currentTimeMillis() + refreshTokenValidityInMilliseconds);

        long remainTime = expiration.getTime() - compareDate.getTime();

        //then
        Assertions.assertThat(remainTime <= refreshTokenValidityInMilliseconds).isTrue();

    }



    private User getUser() {
        User user = User.builder()
                .username("username")
                .nickname("nickname")
                .email("test@naver.com")
                .role(Role.STUDENT.getKey())
                .build();
        return user;
    }
    private AccessTokenUserRequest convertToUser(User user) {
        return AccessTokenUserRequest.builder()
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
    private String createTempRefreshToken(AccessTokenUserRequest userDTO, Long now) {
        return Jwts.builder()
                .setSubject(userDTO.getEmail())
                .setExpiration(new Date(now + refreshTokenValidityInMilliseconds * 2))
                .signWith(SignatureAlgorithm.HS512, refreshSecret)
                .compact();
    }

}