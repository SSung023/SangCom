package Project.SangCom.post.domain;

import Project.SangCom.comment.domain.Comment;
import Project.SangCom.like.domain.Likes;
import Project.SangCom.post.dto.PostRequest;
import Project.SangCom.scrap.domain.Scrap;
import Project.SangCom.user.domain.User;
import Project.SangCom.util.formatter.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@ToString(callSuper = true, includeFieldNames = true)
@Getter
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Likes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Scrap> scraps = new ArrayList<>();


    private String author; // 게시글 작성자 이름

    @Enumerated(EnumType.STRING)
    private PostCategory category; // 게시글 카테고리(분류)

    @NotNull
    @Column(length = 30)
    private String title;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String content;

    private int commentCount; // 댓글 수
    private int likeCount; // 좋아요 수

    /**
     * TINYINT
     * 0: false, 1: true
     */
    @Column(columnDefinition = "TINYINT", length = 1)
    private int isAnonymous;
    @Column(columnDefinition = "TINYINT", length = 1)
    private int isDeleted;

    // Only 건의게시판
    @Column(columnDefinition = "TINYINT", length = 1)
    private int isSecret;
    @Column(columnDefinition = "TINYINT", length = 1)
    private int isSolved;


    public Post() {
    }

    @Builder
    public Post(String author, PostCategory category, String title, String content
    , int isAnonymous, int isDeleted, int isSecret, int isSolved) {
        this.author = author;
        this.category = category;
        this.title = title;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.isDeleted = isDeleted;
        this.isSecret = isSecret;
        this.isSolved = isSolved;
    }


    //== 비즈니스 코드 ==//
    // 게시글의 content(내용)을 수정
    public void updatePost(PostRequest postRequest){
        if (!postRequest.getContent().equals("")) {
            this.content = postRequest.getContent();
        }
    }
    // 게시글 삭제 처리
    public void deletePost(){
        this.isDeleted = 1; // true
    }

    /**
     * 댓글 수 갱신
     * @param changeAmt 댓글 수의 변화량
     */
    public void updateCommentCnt(int changeAmt){
        this.commentCount += changeAmt;
    }

    /**
     * 좋아요 수 갱신
     * @param changeAmt 좋아요 수의 변화량
     */
    public void updateLikes(int changeAmt){
        this.likeCount += changeAmt;
    }




    //=== 연관관계 편의 메서드 ===//
    public void addUser(User user){
        this.user = user;
        user.getPosts().add(this);
    }
}
