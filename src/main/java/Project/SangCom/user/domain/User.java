package Project.SangCom.user.domain;

import Project.SangCom.user.domain.embedded.StudentInfo;
import Project.SangCom.user.domain.embedded.TeacherInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * 회원 엔티티
 * email: kakao 소셜 로그인 이후 kakao email을 받아옴
 * nickname, info: 소셜 로그인 이후 자체 회원가입 시 기입
 * role: student, student_council, teacher, admin
 */
@Entity
@Getter @Setter
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 20)
    @NotNull
    private String nickname;

    @Column(unique = true, length = 30)
    @NotNull
    private String email;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Role role;

    @Embedded
    private StudentInfo studentInfo;
    @Embedded
    private TeacherInfo teacherInfo;
}
