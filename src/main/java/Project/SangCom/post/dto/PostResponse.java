package Project.SangCom.post.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class PostResponse {

    private Long id; // PK
    private String boardCategory; // 게시글 카테고리
    private String author; // 익명 or 닉네임
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private int isAnonymous; // 익명 여부

    @Builder
    public PostResponse(Long id, String boardCategory, String author, String title, String content,
                        int isAnonymous) {
        this.id = id;
        this.boardCategory = boardCategory;
        this.author = author;
        this.title = title;
        this.content = content;
        this.isAnonymous = isAnonymous;
    }
}
