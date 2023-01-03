package Project.SangCom.user.domain.embedded;

import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

/**
 * User 테이블에 들어갈 학생 정보 -> 학년, 반, 번호
 * null 가능
 */
@Embeddable
@NoArgsConstructor
public class StudentInfo {

    private Integer grade;
    private Integer classes;
    private Integer number;
}
