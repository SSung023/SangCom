package Project.SangCom.user.domain.embedded;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

/**
 * User 테이블에 들어갈 교사 정보 -> 담당 학년, 담당 과목
 * null 가능
 */
@Embeddable
@Getter
@ToString
@NoArgsConstructor
public class TeacherInfo {

    private String chargeGrade;
    private String chargeSubject;
    private String statusMessage; // 상태 메시지

    @Builder
    public TeacherInfo(String chargeGrade, String chargeSubject, String statusMessage) {
        this.chargeGrade = chargeGrade;
        this.chargeSubject = chargeSubject;
        this.statusMessage = statusMessage;
    }

    @Override
    public boolean equals(Object obj) {
        TeacherInfo target = (TeacherInfo) obj;

        return (Objects.equals(this.chargeGrade, target.getChargeGrade()))
                && (Objects.equals(this.chargeSubject, target.getChargeSubject()));
    }
}
