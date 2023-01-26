package Project.SangCom.utils;

import Project.SangCom.user.domain.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    String username() default "username";
    String nickname() default "nickname";
    String email() default "test@naver.com";
    Role role() default Role.STUDENT;

}