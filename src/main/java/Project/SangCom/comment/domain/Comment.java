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
import java.util.Optional;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    //== 부모 댓글을 삭제해도 자식 댓글은 남아있음 ==//
    @OneToMany(mappedBy = "parent")
    private List<Comment> childList = new ArrayList<>();

    /*@OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<Likes> likes = new ArrayList<>();*/

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
    public Comment(User user, Post post, Comment parent,
            String author, String content, Timestamp date, int isAnonymous, int isDeleted){
        this.user = user;
        this.post = post;
        this.parent = parent;

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
    public void delComment(){
        this.isDeleted = 1;
    }

    public List<Comment> findRemoveableList(){
        List<Comment> result = new ArrayList<>();

        Optional.ofNullable(this.parent).ifPresentOrElse(
                parentComment -> { // 부모 댓글이 존재하는 대댓글인 경우
                    if(parentComment.getIsDeleted() == 1 && parentComment.isAllChildDel() == 1){
                        result.addAll(parentComment.getChildList());
                        result.add(parentComment);
                    }
                },

                () -> { // 댓글(부모)인 경우 -> parent = null
                    if (isAllChildDel() == 1) {
                        result.add(this);
                        result.addAll(this.getChildList());
                    }
                }
        );
        return result;
    }

    private int isAllChildDel(){
        return getChildList().stream()
                .map(Comment::getIsDeleted) // 지워졌는지 여부로 바꾼다
                .filter(isDeleted -> getIsDeleted() == 0) // 지워졌으면 1, 안지워졌으면 0 -> filter에 걸러지는 것 = 0인 것들, 있다면 0, 없다면 orElse를 통해 1 반환
                .findAny() // 지워지지 않은게 하나라도 있다면 0을 반환 -> filter에서 걸러지지 않은 것
                .orElse(1); // 모두 지워졌다면 1을 반환
    }


    //=== 연관관계 편의 메서드 ===//
    public void addUser(User user){
        this.user = user;
        user.getComments().add(this);
    }

    public void setPost(Post post){
        this.post = post;
        post.getComments().add(this);
    }

    public void setParent(Comment parent){
        this.parent = parent;
        parent.getChildList().add(this);
    }

    public void addChild(Comment child){
        childList.add(child);
    }


}
