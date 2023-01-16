package Project.SangCom.security.service;

import Project.SangCom.user.domain.User;
import Project.SangCom.user.dto.UserLoginResponse;
import Project.SangCom.user.service.UserService;
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ExMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;


/**
 * JwtTokenProvider를 통해
 */
@Service
@RequiredArgsConstructor
public class JwtTokenProviderService {

    private final JwtTokenProvider tokenProvider;
    private final UserService userService;


    // access-token 발급 후 Authorization 헤더에 추가
    public String setAccessToken(HttpServletResponse response, User user) {
        String accessToken = tokenProvider.createAccessToken(user);
        response.setHeader("Authorization", accessToken);
        return accessToken;
    }

    // refresh-token 발급 후, Set-Cookie에 추가
    public String setRefreshToken(HttpServletResponse response, User user) {
        String refreshToken = tokenProvider.createRefreshToken(user);
        tokenProvider.setHttpOnlyCookie(response, refreshToken);
        return refreshToken;
    }


    // request의 header에서 access-token을 추출하고, 앞의 prefix를 제거하여 반환
    public String resolveToken(HttpServletRequest request) {
        String token = tokenProvider.resolveTokenFromHeader(request);
        return token;
    }

    // 해당 토큰이 유효한지 확인
    public boolean validateToken(String token) {
        return tokenProvider.validateToken(token);
    }

    // 토큰으로부터 정보를 요청하는 사용자가 누구인지 확인하고, 필요한 정보를 Response DTO 객체로 반환
    public UserLoginResponse getRequestUserInfo(String accessToken) {
        String userEmail = tokenProvider.getUserPk(accessToken);
        Optional<User> userByEmail = userService.findUserByEmail(userEmail);

        if (userByEmail.isEmpty())
            throw new BusinessException(ExMessage.DATA_ERROR_NOT_FOUND);

        User user = userByEmail.get();
        UserLoginResponse loginResponse = UserLoginResponse.builder()
                .role(user.getRole())
                .nickname(user.getNickname())
                .username(user.getUsername())
                .build();

        loginResponse.setInfoByRole(user.getRole(), user.getStudentInfo(), user.getTeacherInfo());

        return loginResponse;
    }

}
