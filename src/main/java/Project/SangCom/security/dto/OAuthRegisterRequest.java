package Project.SangCom.security.dto;

import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.domain.embedded.StudentInfo;
import Project.SangCom.user.domain.embedded.TeacherInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class OAuthRegisterRequest {

    private String role;
    private String email;
    private String nickname;
    private String username;

    // 학생 정보
    private String grade;
    private String classes;
    private String numbers;

    // 교사 정보
    private String chargeGrade;
    private String chargeSubject;


    public User toEntity(){
        Role targetRole;
        if (role.equals("ROLE_STUDENT")) {
            targetRole = Role.STUDENT;
        }
        else if (role.equals("ROLE_TEACHER")) {
            targetRole = Role.TEACHER;
        }

        User user = User.builder()
                .email(email)
                .nickname(nickname)
                .username(username)
                .studentInfo(StudentInfo.builder().grade(grade).classes(classes).number(numbers).build())
                .teacherInfo(TeacherInfo.builder().chargeGrade(chargeGrade).chargeSubject(chargeSubject).build())
                .build();

        return user;
    }
}
