package Project.SangCom.user.dto;

import Project.SangCom.user.domain.User;

import java.io.Serializable;

/**
 * 인증된 사용자 정보(e-mail)를 저장하는 클래스
 */
public class SessionUser implements Serializable {
    private String email;

    public SessionUser(User user) {
        this.email = user.getEmail();
    }
}