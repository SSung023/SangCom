package Project.SangCom.user.domain.embedded;

import lombok.*;

import javax.persistence.Embeddable;
import java.util.Objects;

/**
 * User 테이블에 들어갈 학생 정보 -> 학년, 반, 번호
 * null 가능
 */
@Embeddable
@Getter
@ToString
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

    @Override
    public boolean equals(Object obj) {
        StudentInfo target = (StudentInfo) obj;

        return (Objects.equals(this.grade, target.getGrade()))
                && (Objects.equals(this.classes, target.getClasses()))
                && (Objects.equals(this.number, target.getNumber()));
    }
}
