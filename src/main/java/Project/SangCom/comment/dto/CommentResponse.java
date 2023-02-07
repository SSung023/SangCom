package Project.SangCom.comment.dto;

import Project.SangCom.comment.domain.Comment;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.security.Timestamp;

@Data
@NoArgsConstructor
public class CommentResponse {

    private Long id; // PK
    private String authorName; // 댓글 작성자 이름 (익명/닉네임)
    private Timestamp date; // 댓글 작성 시간
    private String content; // 댓글 내용
    private int isOwner; // 댓글 작성자 여부
    private int isAnonymous; // 익명 여부

    public static int TRUE = 1, FALSE = 0;

    /* Entity -> Dto*/
    @Builder
    public CommentResponse(Long id, String authorName, Timestamp date, String content, int isOwner, int isAnonymous) {
        this.id = id;
        this.authorName = authorName;
        this.date = date;
        this.content = content;
        this.isOwner = isOwner;
        this.isAnonymous = isAnonymous;
    }

    /**
     * 댓글 작성, 조회 이후의 Response에 isOwner값이 설정되어 있어야 한다.
     * @param isOwner 댓글 작성자 여부
     */
    public void setIsOwner(int isOwner) {
        this.isOwner = isOwner;
    }
}
