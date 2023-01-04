package Project.SangCom.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    // 학생, 학생회, 교사, 관리자
    STUDENT("ROLE_STUDENT", "학생"),
    STUDENT_COUNCIL("ROLE_STUDENT_COUNCIL", "학생회"),
    TEACHER("ROLE_TEACHER", "교사"),
    ADMIN("ROLE_ADMIN", "관리자");

    //각 권한이 가질 필드 선언 + 생성자 주입
    private final String key;
    private final String title;
}
