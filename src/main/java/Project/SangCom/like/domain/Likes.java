package Project.SangCom.like.domain;

import Project.SangCom.comment.domain.Comment;
import Project.SangCom.post.domain.Post;
import Project.SangCom.user.domain.User;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;


/**
 *
 */
@Entity
@Getter
@ToString(exclude = {"post", "comment"})
public class Likes {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;


    //=== 연관관계 편의 메서드 ===//
    public void setUser(User user){
        this.user = user;
        user.getLikes().add(this);
    }

    public void setPost(Post post){
        this.post = post;
        post.getLikes().add(this);
    }

    public void setComment(Comment comment){
        this.comment = comment;
        comment.getLikes().add(this);
    }


    @Override
    public boolean equals(Object obj) {
        Likes target = (Likes) obj;
        return Objects.equals(this.id, target.getId());
    }
}
