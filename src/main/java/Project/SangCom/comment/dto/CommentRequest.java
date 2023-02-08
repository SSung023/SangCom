package Project.SangCom.comment.dto;

import Project.SangCom.comment.domain.Comment;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Timestamp;

@Data
@NoArgsConstructor
public class CommentRequest {

    private Long Id;
    private String authorName;
    private Timestamp date;
    private String content;
    private int isAnonymous;

    @Builder
    public CommentRequest(Long Id, String authorName, Timestamp date, String content, int isAnonymous){
        this.Id = Id;
        this.authorName = authorName;
        this.date = date;
        this.content = content;

        this.isAnonymous = isAnonymous;
    }

    public Comment toEntity(){
        return Comment.builder()
                .author(this.authorName)
                .date(this.date)
                .content(this.content)
                .isAnonymous(this.isAnonymous)
                .build();
    }

}
