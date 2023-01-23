package Project.SangCom.security.service;

import Project.SangCom.security.dto.AccessTokenUserRequest;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.dto.UserLoginResponse;
import Project.SangCom.user.service.UserService;
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static Project.SangCom.security.service.JwtTokenProvider.AUTHORIZATION_HEADER;


/**
 * JwtTokenProvider를 통해
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProviderService {

    private final JwtTokenProvider tokenProvider;
    private final UserService userService;


    // access-token 발급 후 Authorization 헤더에 추가
    public String setAccessToken(HttpServletResponse response, User user) {
        AccessTokenUserRequest userDTO = convertToUser(user);
        String accessToken = tokenProvider.createAccessToken(userDTO);
        response.setHeader("Authorization", accessToken);
        return accessToken;
    }

    // refresh-token 발급 후, Set-Cookie에 추가
    public String setRefreshToken(HttpServletResponse response, User user) {
        AccessTokenUserRequest userDTO = convertToUser(user);
        String refreshToken = tokenProvider.createRefreshToken(userDTO);
        tokenProvider.setHttpOnlyCookie(response, refreshToken);
        return refreshToken;
    }


    // request의 header에서 access-token을 추출하고, 앞의 prefix를 제거하여 반환
    public String resolveAccessToken(HttpServletRequest request) {
        String token = tokenProvider.resolveAccessTokenFromHeader(request);
        return token;
    }

    /**
     * 1. access-token의 유효성을 검사
     * 2-1. access-token이 유효하다면 true 반환
     * 2-2. access-token이 유효하지 않다면 refresh-token 유효성 확인: 유효하다면 access-token 재발급 -> response header에 추가
     * @param accessToken 유효성을 확인할 토큰 대상
     */
    public boolean validateAndReissueToken(HttpServletRequest request, HttpServletResponse response, String accessToken) {
        // access-token이 유효하지 않을 때
        if (!tokenProvider.validateAccessToken(accessToken)){
            log.info("access-token 유효하지 않음");
            String refreshToken = tokenProvider.resolveRefreshTokenFromHeader(request);

            // refresh-token 유효성 확인 후, access-token 재발급
            if (tokenProvider.validateRefreshToken(refreshToken)) {
                log.info("refresh-token가 유효하므로 access-token을 재발급합니다.");
                String email = tokenProvider.getUserPkByRefresh(refreshToken);
                User accessUser = getAccessUserInfo(email);
                AccessTokenUserRequest userDTO = convertToUser(accessUser);

                String newAccessToken = tokenProvider.createAccessToken(userDTO);
                response.setHeader(AUTHORIZATION_HEADER, newAccessToken);

                // refresh-token의 남은 유효기간을 확인하고, 유효기간이 1/2 이하라면 refresh-token 재발급
                if (tokenProvider.checkRefreshExpirationTime(refreshToken)){
                    String newRefreshToken = tokenProvider.createRefreshToken(userDTO);
                    tokenProvider.setHttpOnlyCookie(response, newRefreshToken);
                }
                return true;
            }
            // refresh-token도 유효하지 않을 때
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
            //return false;
        }
        // access-token이 유효할 때
        return true;
    }

    /**
     * refresh-token에서 얻은 email을 통해 access-token에 필요한 정보를 담아 User 객체를 만든다.
     * User 객체가 아닌 AccessUser 같은 DTO에 담는 것이 더 좋을까?
     */
    public User getAccessUserInfo(String email) {
        Optional<User> userByEmail = userService.findUserByEmail(email);
        if (userByEmail.isEmpty()){
            throw new BusinessException(ErrorCode.SAVED_MEMBER_NOT_FOUND);
        }

        return userByEmail.get();
    }



    // 토큰으로부터 정보를 요청하는 사용자가 누구인지 확인하고, 필요한 정보를 Response DTO 객체로 반환
    public UserLoginResponse getRequestUserInfo(String accessToken) {
        String userEmail = tokenProvider.getUserPk(accessToken);
        Optional<User> userByEmail = userService.findUserByEmail(userEmail);

        if (userByEmail.isEmpty())
            throw new BusinessException(ErrorCode.SAVED_MEMBER_NOT_FOUND);

        User user = userByEmail.get();
        UserLoginResponse loginResponse = UserLoginResponse.builder()
                .role(user.getRole())
                .nickname(user.getNickname())
                .username(user.getUsername())
                .build();

        loginResponse.setInfoByRole(user.getRole(), user.getStudentInfo(), user.getTeacherInfo());

        return loginResponse;
    }

    public Authentication getAuthentication(String token){
        Authentication authentication = tokenProvider.getAuthentication(token);
        return authentication;
    }



    private AccessTokenUserRequest convertToUser(User user) {
        return AccessTokenUserRequest.builder()
                .email(user.getEmail())
                .role(user.getRole().getKey())
                .build();
    }
}
