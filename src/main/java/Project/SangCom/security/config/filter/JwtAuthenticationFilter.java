package Project.SangCom.security.config.filter;

import Project.SangCom.security.service.JwtTokenProviderService;
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

import static Project.SangCom.security.config.SecurityConfig.permitURI;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProviderService tokenProviderService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        /**
         * 요청한 uri가 permitAll()로 설정한 uri인 경우 JWT 토큰이 없는 상태이므로 에러를 출력하고 넘어감
         */
        if (request.getHeader("Authorization") == null || !request.getHeader("Authorization").startsWith("Bearer ")){
            if (isPermitUri(requestURI)){
                log.info("permitURI에 requestURI가 있으므로 JWT filter를 적용하지 않습니다.");

                // JWT 토큰이 없는 경우에는 Authentication를 null로 설정
                SecurityContextHolder.getContext().setAuthentication(null);
                filterChain.doFilter(request, response);
                return;
            }
        }

        /**
         * 1. header의 access-token을 추출
         * 2. access-token이 유효한 경우 tokenProviderService에게서 Authentication 정보 추출
         * 3. 전달받은 Authentication 객체를 SecurityContext에 저장
         * 4. filterChain.doFilter 실행
         */
        String accessToken = tokenProviderService.resolveAccessToken(request);

        if (StringUtils.hasText(accessToken) && tokenProviderService.validateAndReissueToken(request, response, accessToken)) {
            Authentication authentication = tokenProviderService.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}",
                    authentication.getName(), requestURI);
        }
        else {

            log.debug("유효한 JWT 토큰이 없습니다. uri: " + requestURI);
            throw new BusinessException("유효한 JWT 토큰이 없습니다.");
        }

        filterChain.doFilter(request, response);
    }


    // requestURI가 permitAll로 설정한 uri에 해당하는지 확인
    private boolean isPermitUri(String requestURI){
        for (String uri : permitURI) {
            uri = uri.replace("*","");
            if (requestURI.contains(uri)){
                return true;
            }
        }
        return false;
    }

}
