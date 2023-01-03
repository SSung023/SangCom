package Project.SangCom.user.domain.embedded;

import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

/**
 * User 테이블에 들어갈 교사 정보 -> 담당 학년, 담당 과목
 * null 가능
 */
@Embeddable
@NoArgsConstructor
public class TeacherInfo {

    private String chargeGrade;
    private String chargeSubject;
}
