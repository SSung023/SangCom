package Project.SangCom.post.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString
public class PostResponse {

    private Long id; // PK
    private String boardCategory; // 게시글 카테고리
    private String author; // 익명 or 닉네임
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private int likeCount; // 좋아요 수
    private int isLikePressed; // 좋아요 눌렀는지 여부
    private int isOwner; // 게시글 작성자 여부
    private int isAnonymous; // 익명 여부
    private LocalDateTime createdDate; // 생성된 날짜

    public static int TRUE = 1, FALSE = 0;

    @Builder
    public PostResponse(Long id, String boardCategory, String author, String title, String content,
                        int likeCount, int isLikePressed, int isOwner, int isAnonymous, LocalDateTime createdDate) {
        this.id = id;
        this.boardCategory = boardCategory;
        this.author = author;
        this.title = title;
        this.content = content;
        this.likeCount = likeCount;
        this.isLikePressed = isLikePressed;
        this.isOwner = isOwner;
        this.isAnonymous = isAnonymous;
        this.createdDate = createdDate;
    }

    /**
     * 게시글 작성, 수정, 조회 이후의 Response에 isOwner값이 설정되어 있어야 한다.
     * @param isOwner 게시글 작성자 여부
     */
    public void setIsOwner(int isOwner) {
        this.isOwner = isOwner;
    }

    /**
     * 게시글 상세 조회 이후의 Response에 isLikePressed 값이 설정되어 있어야 한다.
     * @param isLikePressed 좋아요 누른 여부
     */
    public void setIsLikePressed(int isLikePressed){
        this.isLikePressed = isLikePressed;
    }
}
