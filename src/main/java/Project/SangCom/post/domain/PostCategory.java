package Project.SangCom.post.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PostCategory {
    // 자유게시판
    FREE("자유게시판"),
    GRADE1("1학년게시판"),
    GRADE2("2학년게시판"),
    GRADE3("3학년게시판"),
    COUNCIL("학생회게시판"),
    SUGGESTION("건의게시판"),
    CLUB("동아리게시판")
    ;

    private final String key;
}
