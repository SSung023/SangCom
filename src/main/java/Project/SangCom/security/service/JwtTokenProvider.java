package Project.SangCom.security.service;


import Project.SangCom.security.dto.AccessTokenUserRequest;
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
    @Value("${jwt.refresh-secret}")
    private String refreshSecretKey;
    @Value("${jwt.access-token-validity-in-seconds}")
    private Long accessTokenValidityInMilliseconds;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenValidityInMilliseconds;

    private static String AUTHORITIES_KEY = "role";
    private static String EMAIL_KEY = "email";
    public static String AUTHORIZATION_HEADER = "Authorization";
    public static String REFRESH_HEADER = "Set-Cookie";

    public int refreshLength = 0;
    private final CustomUserDetailsService customUserDetailsService;


    /**
     * access-token & refresh-token 생성
     */
    public String createAccessToken(AccessTokenUserRequest userDTO) {
         Long now = System.currentTimeMillis();

         String accessToken = Jwts.builder()
                 .setHeader(createHeader())
                 .setClaims(createClaims(userDTO))
                 .setSubject(userDTO.getEmail())
                 .setExpiration(new Date(now + accessTokenValidityInMilliseconds))
                 .signWith(SignatureAlgorithm.HS512, secretKey)
                 .compact();
         return "Bearer " + accessToken;
    }
    public String createRefreshToken(AccessTokenUserRequest userDTO){
        Long now = System.currentTimeMillis();

        String refreshToken = Jwts.builder()
                .setHeader(createHeader())
                .setClaims(createClaims(userDTO))
                .setSubject(userDTO.getEmail())
                .setExpiration(new Date(now + refreshTokenValidityInMilliseconds * 2))
                .signWith(SignatureAlgorithm.HS512, refreshSecretKey)
                .compact();
        
        refreshLength = refreshToken.length();

        return refreshToken;
    }
    public void setHttpOnlyCookie(HttpServletResponse response, String refreshToken){
        ResponseCookie cookie
                = ResponseCookie.from("refreshToken", refreshToken)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .maxAge(6 * 60 * 60) // 6hours * 60min * 60sec
                .build();

        response.setHeader(REFRESH_HEADER, cookie.toString());
    }
    public Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS512");
        return header;
    }
    public Map<String, Object> createClaims(AccessTokenUserRequest userDTO) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(EMAIL_KEY, userDTO.getEmail());
        claims.put(AUTHORITIES_KEY, userDTO.getRole());
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
     * Set-Cookie 헤더에서 refresh token을 추출하여 반환
     * - 오버로딩: Request.ver, String.ver
     */
    public String resolveRefreshTokenFromHeader(HttpServletRequest request) {
        int tokenStartIdx = 13;
        String cookieHeader = request.getHeader(REFRESH_HEADER);
        String refreshToken = cookieHeader.substring(tokenStartIdx, tokenStartIdx + refreshLength);

        return refreshToken;
    }
    public String resolveRefreshTokenFromHeader(String fullRefreshToken) {
        int tokenStartIdx = 13;
        String refreshToken = fullRefreshToken.substring(tokenStartIdx, tokenStartIdx + refreshLength);

        return refreshToken;
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
     * 전달받은 access-token이 유효한지 확인 후, 유효하지 않다면 오류 발생
     */
    public boolean validateAccessToken(String token) {
        return validateToken(token, secretKey);
    }

    /**
     * 전달받은 refresh-token이 유효한지 확인 후, 유효하지 않다면 오류 발생
     */
    public boolean validateRefreshToken(String token){
        return validateToken(token, refreshSecretKey);
    }

    private boolean validateToken(String token, String secretKey) {
        try{
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        }
        catch (SignatureException e){
            log.error("Invalid JWT access signature", e);
//            throw new BusinessException(ExMessage.TOKEN_INVALID_SIGNATURE);
        }
        catch (MalformedJwtException e){
            log.error("Invalid JWT access token", e);
//            throw new BusinessException(ExMessage.TOKEN_INVALID);
        }
        catch (ExpiredJwtException e){
            log.error("Expired JWT access token", e);
//            throw new BusinessException(ExMessage.TOKEN_EXPIRED);
        }
        catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT access token", e);
//            throw new BusinessException(ExMessage.TOKEN_UNSUPPORTED);
        }
        catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty", e);
//            throw new BusinessException(ExMessage.TOKEN_INVALID_CLAIM);
        }
        return false;
    }



    /**
     * refreshToken의 만료 시간이 1/2이 지났는지 여부 반환
     */
    public Boolean checkRefreshExpirationTime(String refreshToken) {
        Claims claims = Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(refreshToken).getBody();

        Date expiration = claims.getExpiration();
        Date curDate = new Date(System.currentTimeMillis());

        long remainTime = expiration.getTime() - curDate.getTime();

        // 남은 시간이 1/2 이하로 남았다면 refreshToken을 재발급해야 하므로 true 반환
        return remainTime <= refreshTokenValidityInMilliseconds;
    }

    /**
     * access-token에서 DB에서 사용자를 찾을 때 사용할 정보(이메일)를 추출
     */
    public String getUserPk(String token) {
        token = resolveTokenFromString(token);
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * refresh-token에서 사용자 정보 email을 추출
     */
    public String getUserPkByRefresh(String token) {
        token = resolveTokenFromString(token);
        return Jwts.parser()
                .setSigningKey(refreshSecretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    /**
     * token에서 추출한 사용자 식별 정보(email)를 토대로 Authentication 객체 생성 후 반환
     */
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
