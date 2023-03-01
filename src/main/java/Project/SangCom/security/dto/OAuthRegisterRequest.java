package Project.SangCom.security.dto;

import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.domain.embedded.StudentInfo;
import Project.SangCom.user.domain.embedded.TeacherInfo;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@ToString
@NoArgsConstructor
@Slf4j
public class OAuthRegisterRequest {

    private String role;
    private String email;
    private String nickname;
    private String username;

    // 학생 정보
    private String grade;
    private String classes;
    private String number;

    // 교사 정보
    private String chargeGrade;
    private String chargeSubject;


    @Builder
    public OAuthRegisterRequest(String role, String email, String nickname, String username, String grade, String classes, String number, String chargeGrade, String chargeSubject) {
        this.role = role;
        this.email = email;
        this.nickname = nickname;
        this.username = username;
        this.grade = grade;
        this.classes = classes;
        this.number = number;
        this.chargeGrade = chargeGrade;
        this.chargeSubject = chargeSubject;
    }

    public User toEntity(){
        log.info("register request: " + this.toString());
        if (role.equals("student")) {
            role = Role.STUDENT.getKey();
        }
        else if (role.equals("teacher")) {
            role = Role.TEACHER.getKey();
        }

        User user = User.builder()
                .role(role)
                .email(email)
                .nickname(nickname)
                .username(username)
                .studentInfo(StudentInfo.builder().grade(grade).classes(classes).number(number).build())
                .teacherInfo(TeacherInfo.builder().chargeGrade(chargeGrade).chargeSubject(chargeSubject).build())
                .build();

        return user;
    }
}
