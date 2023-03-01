package Project.SangCom.utils;

import Project.SangCom.security.service.CustomUserDetailsService;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;


@RequiredArgsConstructor
@Slf4j
public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    private final UserService service;
    private final CustomUserDetailsService userDetailsService;
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        User tmp = User.builder()
                .email(customUser.email())
                .role(customUser.role().getKey())
                .build();
        User user = User.builder()
                .email(customUser.email())
                .username(customUser.username())
                .nickname(customUser.nickname())
                .role(customUser.role().getKey())
                .build();

        service.saveUser(tmp);
        service.registerUser(user);

        UserDetails principal = userDetailsService.loadUserByUsername(customUser.email());
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        return securityContext;
    }
}
