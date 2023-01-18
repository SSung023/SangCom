package Project.SangCom.security.service;


import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static Project.SangCom.security.service.JwtTokenProvider.AUTHORIZATION_HEADER;
import static Project.SangCom.security.service.JwtTokenProvider.REFRESH_HEADER;

@SpringBootTest
@Transactional
@Slf4j
@ActiveProfiles({"jwt"})
public class JwtTokenProviderServiceTest {
    @Value("${jwt.secret}")
    String secretKey;
    @Value("${jwt.refresh-secret}")
    String refreshSecretKey;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenValidityInMilliseconds;

    @Autowired
    UserRepository repository;
    @Autowired
    JwtTokenProviderService tokenProviderService;
    @Autowired
    JwtTokenProvider provider;
    

    @Test
    @DisplayName("access-token이 유효하지 않으면 refresh-token의 유효성을 확인해야 한다.")
    public void validateRefreshToken(){
        //given
        User user = getUser();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        repository.save(user);

        Long now = System.currentTimeMillis();
        String accessToken = getToken(user, now + 100, secretKey); // 유효기간 짧은 access-token 생성
        String refreshToken = provider.createRefreshToken(user);

        ResponseCookie cookie
                = ResponseCookie.from("refreshToken", refreshToken)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .maxAge(24 * 60 * 60) // 24hour * 60min * 60sec
                .build();

        //when
        request.addHeader(AUTHORIZATION_HEADER, accessToken);
        request.addHeader(REFRESH_HEADER , cookie.toString());

        log.info(cookie.toString());

        //then
        tokenProviderService.validateAndReissueToken(request, response, accessToken);

    }


    @Test
    @DisplayName("refresh-token이 유효하다면, refresh-token에서 access-token에 필요한 정보를 받아올 수 있다.")
    public void getUserInfoByEmail(){
        //given
        User user = getUser();
        repository.save(user);

        //when
        String refreshToken = provider.createRefreshToken(user);
        String userEmail = provider.getUserPkByRefresh(refreshToken);
        User accessUser = tokenProviderService.getAccessUserInfo(userEmail);

        //then
        Assertions.assertThat(user.getEmail()).isEqualTo(accessUser.getEmail());
        Assertions.assertThat(user.getRole()).isEqualTo(accessUser.getRole());
    }
    
    @Test
    @DisplayName("access-token은 유효하지 않고, refresh-token은 유효하다면 access-token을 재발급한다.")
    public void reissueAccessToken(){
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        User user = getUser();
        repository.save(user);

        //when
        Long now = System.currentTimeMillis();
        String accessToken = "Bearer " + getToken(user, now + 100, secretKey); // 유효기간이 짧은 access-token 생성
        String refreshToken = provider.createRefreshToken(user);
        ResponseCookie cookie
                = ResponseCookie.from("refreshToken", refreshToken)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .maxAge(24 * 60 * 60) // 24hour * 60min * 60sec
                .build();

        request.addHeader(AUTHORIZATION_HEADER, accessToken);
        request.addHeader(REFRESH_HEADER, cookie.toString());

        tokenProviderService.validateAndReissueToken(request, response, accessToken);

        //then
        Assertions.assertThat(accessToken).isNotEqualTo(response.getHeader(AUTHORIZATION_HEADER));
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
    private String getToken(User user, long now, String secretKey) {
        return Jwts.builder()
                .setHeader(createHeader())
                .setClaims(createClaims(user))
                .setSubject(user.getEmail())
                .setExpiration(new Date(now))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }
    private Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS512");
        return header;
    }
    private Map<String, Object> createClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("Authorization", user.getRole().getKey());
        return claims;

    }
}
