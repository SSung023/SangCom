package Project.SangCom.user.domain;

import Project.SangCom.user.domain.embedded.StudentInfo;
import Project.SangCom.user.domain.embedded.TeacherInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * 회원 엔티티
 * email: kakao 소셜 로그인 이후 kakao email을 받아옴
 * nickname, info: 소셜 로그인 이후 자체 회원가입 시 기입
 * role: not_verified, student, student_council, teacher, admin
 */
@Entity
@ToString
@Getter @Setter
public class User implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 20)
    private String nickname;

    @Column(unique = true, length = 30)
    @NotNull
    private String email;

    private String username;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Role role;

    @Embedded
    private StudentInfo studentInfo = new StudentInfo();
    @Embedded
    private TeacherInfo teacherInfo = new TeacherInfo();

    @Builder
    public User(String username, String nickname, String email, Role role, StudentInfo studentInfo, TeacherInfo teacherInfo) {
        // studentInfo, teacherInfo 추가 필요
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
        this.studentInfo = studentInfo;
        this.teacherInfo = teacherInfo;
    }

    public User() {
    }

    @Override
    public boolean equals(Object obj) {
        // StudentInfo, TeacherInfo 제외한 비교
        User target = (User) obj;
        return (Objects.equals(this.id, target.getId())) && (this.nickname.equals(target.nickname))
                && (this.email.equals(target.email)) && (this.username.equals(target.username))
                && (this.role == target.role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authList = new ArrayList<>();
        authList.add(new SimpleGrantedAuthority(this.role.getKey()));
        return authList;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
