package Project.SangCom.security.service;


import Project.SangCom.user.domain.User;
import io.jsonwebtoken.Jwts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTokenProvider {
    private final String secretKey;
    private static final String AUTHORITIES_KEY = "role";
    private static final String EMAIL_KEY = "email";
    private final Long accessTokenValidityInMilliseconds;
    private final Long refreshTokenValidityInMilliseconds;

    public JwtTokenProvider(String secretKey, Long accessTokenValidTime, Long refreshTokenValidTime) {
        this.secretKey = secretKey;
        this.accessTokenValidityInMilliseconds = accessTokenValidTime * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidTime * 1000;
    }


    /**
     *
     */
    public String createAccessToken(User user){
        Long now = System.currentTimeMillis();

        String accessToken =  Jwts.builder()
                .setHeader(createHeader())
                .setClaims(createClaims(user))
                .setSubject("access-token")
                .setExpiration(new Date(now + accessTokenValidityInMilliseconds))
                .compact();

        return accessToken;
    }

    private Map<String, Object> createHeader(){
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");
        return header;
    }

    // JWT's payload
    private Map<String, Object> createClaims(User user){
        Map<String, Object> claims = new HashMap<>();
        claims.put(EMAIL_KEY, user.getEmail());
        claims.put(AUTHORITIES_KEY, user.getRole());
        return claims;
    }


}
