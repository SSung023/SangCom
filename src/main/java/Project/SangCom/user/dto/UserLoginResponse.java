package Project.SangCom.user.dto;

import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.embedded.StudentInfo;
import Project.SangCom.user.domain.embedded.TeacherInfo;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class UserLoginResponse {
    private List<String> role;
    private String username;
    private String nickname;

    private String grade;
    private String classes;
    private String number;

    private String chargeSubject;
    private String chargeGrade;


    @Builder
    public UserLoginResponse(String role, String username, String nickname,
                             String grade, String classes, String number,
                             String chargeSubject, String chargeGrade) {
        this.role = setRoleList(role);
        this.username = username;
        this.nickname = nickname;

        this.grade = grade;
        this.classes = classes;
        this.number = number;

        this.chargeSubject = chargeSubject;
        this.chargeGrade = chargeGrade;

    }

    private List<String> setRoleList(String role){
        List<String> roles = new ArrayList<>();
        for (String r : role.split(",")) {
            roles.add(r.substring(5));
        }
        return roles;
    }

    public void setInfoByRole(String role, StudentInfo studentInfo, TeacherInfo teacherInfo){
        if (role.contains("STUDENT")){
            this.grade = studentInfo.getGrade();
            this.classes = studentInfo.getClasses();
            this.number = studentInfo.getNumber();
            this.chargeSubject = "";
            this.chargeGrade = "";
        }
        else if (role.contains("TEACHER")){
            this.grade = "";
            this.classes = "";
            this.number = "";
            this.chargeSubject = teacherInfo.getChargeSubject();
            this.chargeGrade = teacherInfo.getChargeGrade();
        }
    }
}
