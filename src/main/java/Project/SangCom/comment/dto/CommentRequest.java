package Project.SangCom.comment.dto;

import Project.SangCom.comment.domain.Comment;
import Project.SangCom.comment.service.CommentService;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;


@Data
@NoArgsConstructor
public class CommentRequest {
    private Long parentId;
    private String authorName;
    private String content;
    private int isAnonymous;

    @Builder
    public CommentRequest(Long parentId, String authorName, String content, int isAnonymous){
        this.parentId = parentId;
        this.authorName = authorName;
        this.content = content;

        this.isAnonymous = isAnonymous;
    }

    public Comment toEntity(){
        return Comment.builder()
                //.parent(commentService.findCommentById(parentId))
                .author(this.authorName)
                .content(this.content)
                .isAnonymous(this.isAnonymous)
                .build();
    }

    public void updateAuthor(String nickname) {
        this.authorName = nickname;
    }
}

// 스프링은 DTO를 이용해 사용자에게 Request를 받고, Response를 보냄