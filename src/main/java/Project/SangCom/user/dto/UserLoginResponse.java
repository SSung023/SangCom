package Project.SangCom.user.dto;

import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.embedded.StudentInfo;
import Project.SangCom.user.domain.embedded.TeacherInfo;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserLoginResponse {
    private Role role;
    private String username;
    private String nickname;

    private String grade;
    private String classes;
    private String number;

    private String chargeSubject;
    private String chargeGrade;


    @Builder
    public UserLoginResponse(Role role, String username, String nickname,
                             String grade, String classes, String number,
                             String chargeSubject, String chargeGrade) {
        this.role = role;
        this.username = username;
        this.nickname = nickname;

        this.grade = grade;
        this.classes = classes;
        this.number = number;

        this.chargeSubject = chargeSubject;
        this.chargeGrade = chargeGrade;

    }

    public void setInfoByRole(Role role, StudentInfo studentInfo, TeacherInfo teacherInfo){
        if (role == Role.STUDENT){
            this.grade = studentInfo.getGrade();
            this.classes = studentInfo.getClasses();
            this.number = studentInfo.getNumber();
            this.chargeSubject = "";
            this.chargeGrade = "";
        }
        else if (role == Role.TEACHER){
            this.grade = "";
            this.classes = "";
            this.number = "";
            this.chargeSubject = teacherInfo.getChargeSubject();
            this.chargeGrade = teacherInfo.getChargeGrade();
        }
    }
}
