package Project.SangCom.user.domain.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

/**
 * User 테이블에 들어갈 학생 정보 -> 학년, 반, 번호
 * null 가능
 */
@Embeddable
@Getter
@NoArgsConstructor
public class StudentInfo {

    private String grade;
    private String classes;
    private String number;

    @Builder
    public StudentInfo(String grade, String classes, String number) {
        this.grade = grade;
        this.classes = classes;
        this.number = number;
    }
}
