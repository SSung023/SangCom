package Project.SangCom.post.dto;

import Project.SangCom.post.domain.Post;
import Project.SangCom.post.domain.PostCategory;
import Project.SangCom.util.exception.BusinessException;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class PostRequest {

    private Long id;
    private String boardCategory;
    private String author; // 닉네임
    private String title;
    private String content;
    private int isAnonymous; // 익명 여부. 0: false, 1: true


    @Builder
    public PostRequest(Long id, String boardCategory, String author, String title, String content, int isAnonymous) {
        this.id = id;
        this.boardCategory = boardCategory;
        this.author = author;
        this.title = title;
        this.content = content;

        this.isAnonymous = isAnonymous;
    }

    public Post toEntity(){
        return Post.builder()
                .category(PostCategory.valueOf(boardCategory))
                .author(this.author)
                .title(this.title)
                .content(this.content)
                .isAnonymous(this.isAnonymous)
                .build();
    }
}
