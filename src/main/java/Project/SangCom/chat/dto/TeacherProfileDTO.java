package Project.SangCom.chat.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class TeacherProfileDTO {
    private Long id; // 사용자 PK
    private String name; // 실명
    private String chargeClass; // 담당 반 정보 ex) 2학년 3반
    private String chargeSubject; // 담당 과목 ex) 국어
    private String statusMessage; // 상태 메시지

    @Builder
    public TeacherProfileDTO(Long id, String name, String chargeClass,
                             String chargeSubject, String statusMessage) {
        this.id = id;
        this.name = name;
        this.chargeClass = chargeClass;
        this.chargeSubject = chargeSubject;
        this.statusMessage = statusMessage;
    }
}
