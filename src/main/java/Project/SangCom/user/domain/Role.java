package Project.SangCom.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    // 미인증사용자, 학생, 학생회, 교사, 관리자
    NOT_VERIFIED("ROLE_NOT_VERIFIED","미인증사용자"),
    STUDENT("ROLE_STUDENT", "학생"),
    GRADE1("ROLE_GRADE1", "1학년"),
    GRADE2("ROLE_GRADE2", "2학년"),
    GRADE3("ROLE_GRADE3", "3학년"),
    TAG_EDITOR("ROLE_TAG_EDITOR", "태그 편집자"),
    STUDENT_COUNCIL("ROLE_STUDENT_COUNCIL", "학생회"),
    TEACHER("ROLE_TEACHER", "교사"),
    ADMIN("ROLE_ADMIN", "관리자");



    //각 권한이 가질 필드 선언 + 생성자 주입
    private final String key;
    private final String title;
}
