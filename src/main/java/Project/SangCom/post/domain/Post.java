package Project.SangCom.post.domain;

import Project.SangCom.comment.domain.Comment;
import Project.SangCom.like.domain.Likes;
import Project.SangCom.user.domain.User;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EnableJpaAuditing
@ToString
@Getter
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;


    /**
     * 대안: user의 id를 저장 - private Long author_id;
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Likes> likes = new ArrayList<>();


    private String author; // 게시글 작성자 이름

    @Enumerated(EnumType.STRING)
    private PostCategory category;

    @NotNull
    @Column(length = 50)
    private String title;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;


    @Column(columnDefinition = "TINYINT", length = 1)
    private int is_anonymous;
    @Column(columnDefinition = "TINYINT", length = 1)
    private int is_deleted;

    // Only 건의게시판
    @Column(columnDefinition = "TINYINT", length = 1)
    private int is_secret;
    @Column(columnDefinition = "TINYINT", length = 1)
    private int is_solved;



    //=== 연관관계 편의 메서드 ===//
    public void setUser(User user){
        this.user = user;
        user.getPosts().add(this);
    }
}
