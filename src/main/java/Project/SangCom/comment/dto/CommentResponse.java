package Project.SangCom.comment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CommentResponse {

    private Long id; // PK
    private String authorName; // 댓글 작성자 이름 (익명/닉네임)
    private String content; // 댓글 내용
    private int likeCount; // 댓글 좋아요 수

    private int isLikePressed; // 좋아요 누른 여부
    private int isOwner; // 댓글 작성자 여부
    private int isAnonymous; // 익명 여부
    private int isDeleted; // 삭제 여부
    private LocalDateTime createdDate; // 작성 일자

    private List<CommentResponse> childComment = new ArrayList<>();


    public static int TRUE = 1, FALSE = 0;

    /* Entity -> Dto*/
    @Builder
    public CommentResponse(Long id, String authorName, String content, int likeCount,
                           int isLikePressed, int isOwner, int isAnonymous, int isDeleted,
                           LocalDateTime createdDate, List<CommentResponse> childComment) {
        this.id = id;
        this.authorName = authorName;
        this.content = content;
        this.likeCount = likeCount;

        this.isLikePressed = isLikePressed;
        this.isOwner = isOwner;
        this.isAnonymous = isAnonymous;
        this.isDeleted = isDeleted;

        this.createdDate = createdDate;

        this.childComment = childComment;
    }

    /**
     * 댓글 작성 이후의 Response에 isOwner값이 설정되어 있어야 한다.
     * @param isOwner 댓글 작성자 여부
     */
    public void setIsOwner(int isOwner) {
        this.isOwner = isOwner;
    }

    /**
     * 댓글 조회 이후의 Response에 isLikePressed 값이 설정되어 있어야 한다.
     * @param isLikePressed 좋아요 누른 여부
     */
    public void setIsLikePressed(int isLikePressed){
        this.isLikePressed = isLikePressed;
    }
}
