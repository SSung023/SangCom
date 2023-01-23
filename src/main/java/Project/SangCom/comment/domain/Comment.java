package Project.SangCom.comment.domain;

import Project.SangCom.post.domain.Post;
import Project.SangCom.util.formatter.BaseTimeEntity;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Comment extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(columnDefinition = "TEXT")
    private String content;


    @Override
    public String toString() {
        return super.toString() + " 댓글 내용: " + content;
    }

    //=== 연관관계 편의 메서드 ===//
    public void setPost(Post post){
        this.post = post;
        post.getComments().add(this);
    }


}
