package Project.SangCom.user.domain.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

/**
 * User 테이블에 들어갈 교사 정보 -> 담당 학년, 담당 과목
 * null 가능
 */
@Embeddable
@Getter
@NoArgsConstructor
public class TeacherInfo {

    private String chargeGrade;
    private String chargeSubject;

    @Builder
    public TeacherInfo(String chargeGrade, String chargeSubject) {
        this.chargeGrade = chargeGrade;
        this.chargeSubject = chargeSubject;
    }
}
