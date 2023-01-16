package Project.SangCom.security.config.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
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


        /**
         * /api/auth 혹은 /swagger-ui.html의 경우 JWT 토큰이 없는 상태이므로 에러를 출력하고 넘어간다.
         * 주의!!!) requestURI에 해당하는 경우에만 허용을 해주어야 할 것 같다. 그렇지 않으면 보안상으로 문제가 있지 않을까?
         */
        if (request.getHeader("Authorization") == null || !request.getHeader("Authorization").startsWith("Bearer ")){
            // JWT 토큰이 없는 경우에는 Authentication를 null로 설정
            log.error(request.getRequestURI() + " 요청의 헤더 형식이 올바르지 않으므로 Authentication을 null로 설정합니다.");
            SecurityContextHolder.getContext().setAuthentication(null);

            filterChain.doFilter(request, response);
            return;
        }


        log.info("Authorization 헤더가 유효합니다.");
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
