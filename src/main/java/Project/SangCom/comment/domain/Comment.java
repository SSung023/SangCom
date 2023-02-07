package Project.SangCom.comment.domain;

import Project.SangCom.like.domain.Likes;
import Project.SangCom.post.domain.Post;
import Project.SangCom.user.domain.User;
import Project.SangCom.util.formatter.BaseTimeEntity;
import antlr.ANTLRParser;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.security.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Comment extends BaseTimeEntity {

    // 댓글 id
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    // 댓글 작성한 user id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 댓글이 포함된 게시글 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    // 부모 댓글 id
    //private Long ?

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<Likes> likes = new ArrayList<>();

    private String author; //댓글 작성자 이름

    // 댓글 내용
    @NotNull
    @Column(columnDefinition = "TEXT")
    private String content;

    // 작성일
    @NotNull
    private Timestamp date;

    /**
     * TINYINT
     * 0: false, 1: true
     */
    @Column(columnDefinition = "TINYINT", length = 1)
    private int isAnonymous;
    @Column(columnDefinition = "TINYINT", length = 1)
    private int isDeleted;

    public Comment(){}

    @Builder
    public Comment(String author, String content, Timestamp date, int isAnonymous, int isDeleted){
        this.author = author;
        this.content = content;
        this.date = date;
        this.isAnonymous = isAnonymous;
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return super.toString() + " 댓글 내용: " + content;
    }

    //== 비즈니스 코드 ==//
    public void deleteComment(){
        this.isDeleted = 1;
    }

    //=== 연관관계 편의 메서드 ===//
    public void setPost(Post post){
        this.post = post;
        post.getComments().add(this);
    }

    public void setUser(User user){
        this.user = user;
        user.getComments().add(this);
    }

}
