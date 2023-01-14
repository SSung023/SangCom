//package Project.SangCom.security.service;
//
//
//import Project.SangCom.user.domain.User;
//import io.jsonwebtoken.*;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//@RequiredArgsConstructor
//@Component
//@Slf4j
//public class JwtTokenProvider {
//
//    @Value("${jwt.secret}")
//    private String secretKey;
//    @Value("${jwt.access-token-validity-in-seconds}")
//    private Long accessTokenValidityInMilliseconds;
//    @Value("${jwt.refresh-token-validity-in-seconds}")
//    private Long refreshTokenValidityInMilliseconds;
//
//    private static String AUTHORITIES_KEY = "role";
//    private static String EMAIL_KEY = "email";
//    public static String AUTHORIZATION_HEADER = "Authorization";
//
//    private final CustomUserDetailsService userDetailsService;
//
//
//
//    /**
//     * createAccessToken(): JWT access token 생성
//     * createRefreshToken(): JWT refresh token 생성
//     *
//     */
//    public String createAccessToken(User user){
//        Long now = System.currentTimeMillis();
//
//        String accessToken =  Jwts.builder()
//                .setHeader(createHeader())
//                .setClaims(createClaims(user))
//                .setSubject("access-token")
//                .setExpiration(new Date(now + accessTokenValidityInMilliseconds))
//                .signWith(SignatureAlgorithm.HS512, secretKey)
//                .compact();
//
//        return accessToken;
//    }
//
//    public String createRefreshToken(User user){
//        Long now = System.currentTimeMillis();
//
//        String refreshToken =  Jwts.builder()
//                .setHeader(createHeader())
//                .setClaims(createClaims(user))
//                .setSubject("refresh-token")
//                .setExpiration(new Date(now + refreshTokenValidityInMilliseconds))
//                .compact();
//
//        return refreshToken;
//    }
//
//    private Map<String, Object> createHeader(){
//        Map<String, Object> header = new HashMap<>();
//        header.put("typ", "JWT");
//        header.put("alg", "HS256");
//        return header;
//    }
//    private Map<String, Object> createClaims(User user){
//        // payload
//        Map<String, Object> claims = new HashMap<>();
//        claims.put(EMAIL_KEY, user.getEmail());
//        claims.put(AUTHORITIES_KEY, user.getRole());
//        return claims;
//    }
//
//    /**
//     * HTTP Request 객체에서 JWT 토큰 정보를 꺼내오는 메서드
//     */
//    public String resolveToken(HttpServletRequest request){
//        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
//
//    /**
//     * 해당 Token이 유효성 & 만료 일자 확인
//     */
//    public Boolean validateToken(String token){
//        try{
//            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
//            return true;
//        }
//        catch (SignatureException e){
//            log.error("Invalid JWT signature", e);
//        }
//        catch (MalformedJwtException e){
//            log.error("Invalid JWT token", e);
//        }
//        catch (ExpiredJwtException e){
//            log.error("Expired JWT token", e);
//        }
//        catch (UnsupportedJwtException e) {
//            log.error("Unsupported JWT token", e);
//        }
//        catch (IllegalArgumentException e) {
//            log.error("JWT claims string is empty", e);
//        }
//        return false;
//    }
//
//    /**
//     * JWT 토큰에서 인증 정보 조회
//     */
//    public Authentication getAuthentication(String token){
//        UserDetails userDetails = userDetailsService.loadUserByUsername(getUserPk(token));
//        return new UsernamePasswordAuthenticationToken
//                    (userDetails, "", userDetails.getAuthorities());
//    }
//
//    /**
//     * 토큰에서 회원 정보 추출
//     */
//    public String getUserPk(String token) {
//        return Jwts.parser()
//                .setSigningKey(secretKey)
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }
//
//}
