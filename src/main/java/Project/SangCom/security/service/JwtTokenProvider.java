package Project.SangCom.security.service;


import Project.SangCom.user.domain.User;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access-token-validity-in-seconds}")
    private Long accessTokenValidityInMilliseconds;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenValidityInMilliseconds;

    private static String AUTHORITIES_KEY = "role";
    private static String EMAIL_KEY = "email";
    public static String AUTHORIZATION_HEADER = "Authorization";

    private final CustomUserDetailsService customUserDetailsService;


    /**
     * access-token & refresh-token 생성
     */
    public String createAccessToken(User user) {
         Long now = System.currentTimeMillis();

         String accessToken = Jwts.builder()
                 .setHeader(createHeader())
                 .setClaims(createClaims(user))
                 .setSubject(user.getEmail())
                 .setExpiration(new Date(now + accessTokenValidityInMilliseconds))
                 .signWith(SignatureAlgorithm.HS512, secretKey)
                 .compact();
         return "Bearer " + accessToken;
    }
    public String createRefreshToken(User user){
        Long now = System.currentTimeMillis();

        String refreshToken = Jwts.builder()
                .setHeader(createHeader())
                .setClaims(createClaims(user))
                .setSubject(user.getEmail())
                .setExpiration(new Date(now + refreshTokenValidityInMilliseconds))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();

        return refreshToken;
    }
    public void setHttpOnlyCookie(HttpServletResponse response, String refreshToken){
        ResponseCookie cookie
                = ResponseCookie.from("refreshToken", refreshToken)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        response.setHeader("Set-Cookie", cookie.toString());
    }
    public Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS512");
        return header;
    }
    public Map<String, Object> createClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(EMAIL_KEY, user.getEmail());
        claims.put(AUTHORITIES_KEY, user.getRole().getKey());
        return claims;

    }


    /**
     * Request 객체의 Authorization 헤더에서 Access token을 추출
     */
    public String resolveAccessTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        bearerToken = resolveTokenFromString(bearerToken);

        return bearerToken;
    }

    /**
     * token을 String 형태로 받은 후,
     * access-token인 경우 Bearer prefix를 제거하고 반환
     * refresh-token인 경우 prefix가 없으므로 그대로 반환
     */
    public String resolveTokenFromString(String token){
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }

        return token;
    }

    /**
     * token에서 Claim을 추출하여 반환
     */
    public Claims parseClaims(String token) {
        token = resolveTokenFromString(token);
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

        return claims;
    }

    /**
     * 전달받은 token이 유효한지 확인 후, 유효하지 않다면 오류 발생
     */
    public boolean validateToken(String token) {
        try{
//            String processedToken = resolveTokenFromString(token);
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        }
        catch (SignatureException e){
            log.error("Invalid JWT signature", e);
        }
        catch (MalformedJwtException e){
            log.error("Invalid JWT token", e);
        }
        catch (ExpiredJwtException e){
            log.error("Expired JWT token", e);
        }

        catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token", e);
        }
        catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty", e);
        }
        return false;
    }

    /**
     * token에서 DB에서 사용자를 찾을 때 사용할 정보(이메일)를 추출
     */
    public String getUserPk(String token) {
        token = resolveTokenFromString(token);
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }


//    private final CustomUserDetailsService userDetailsService;
//
//    /**
//     * JWT 토큰에서 인증 정보 조회
//     */
//    public Authentication getAuthentication(String token){
//        UserDetails userDetails = userDetailsService.loadUserByUsername(getUserPk(token));
//        return new UsernamePasswordAuthenticationToken
//                    (userDetails, "", userDetails.getAuthorities());
//    }
}
