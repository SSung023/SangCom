package Project.SangCom.security.config.filter;

import Project.SangCom.util.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String n = "1";
        if (n.equals(1))
            throw new BusinessException("test ");

        filterChain.doFilter(request, response);
    }

    /*private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // resolveToken(): request 헤더안의 Authorization 항목을 통해 JWT 토큰을 얻는다.
        String jwt = jwtTokenProvider.resolveToken(request);
        String requestURI = request.getRequestURI();

        // validateToken(): header에서 얻은 토큰이 유효한 토큰인지 확인
        if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)){
            // 토큰이 유효하면 유저 정보(Authentication)을 받아온다.
            Authentication authentication = jwtTokenProvider.getAuthentication(jwt);

            // Authentication을 Security Context에 저장한다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
        }
        else {
            log.debug("유효한 JWT 토큰이 없습니다. uri: " + requestURI);
            throw new BusinessException("유효한 JWT 토큰이 없습니다.");
        }

        filterChain.doFilter(request, response);
    }*/
}
