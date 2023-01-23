package Project.SangCom.post.dto;

import Project.SangCom.post.domain.Post;
import Project.SangCom.post.domain.PostCategory;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
public class PostRequest {

//    private Long id;
    private String author;
    private String title;
    private String content;
    private String category;
    private int isAnonymous;


    @Builder
    public PostRequest(String author, String title, String content, int isAnonymous) {
        this.author = author;
        this.title = title;
        this.content = content;

        this.isAnonymous = isAnonymous;
    }

    public Post toEntity(){
        return Post.builder()
                .author(this.author)
                .title(this.title)
                .content(this.content)
                .isAnonymous(this.isAnonymous)
                .build();
    }
}
