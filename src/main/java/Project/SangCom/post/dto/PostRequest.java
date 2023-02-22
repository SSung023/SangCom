package Project.SangCom.post.dto;

import Project.SangCom.post.domain.Post;
import Project.SangCom.post.domain.PostCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class PostRequest {

    private String authorNickname; // 닉네임
    private String title;
    private String content;
    private int isAnonymous; // 익명 여부. 0: false, 1: true


    @Builder
    public PostRequest(String authorNickname, String title, String content, int isAnonymous) {
        this.authorNickname = authorNickname;
        this.title = title;
        this.content = content;

        this.isAnonymous = isAnonymous;
    }


    public Post toEntity(PostCategory postCategory){
        return Post.builder()
                .category(postCategory)
                .author(this.authorNickname)
                .title(this.title)
                .content(this.content)
                .isAnonymous(this.isAnonymous)
                .build();
    }

    public void updateAuthor(String nickname) {
        this.authorNickname = nickname;
    }
}
