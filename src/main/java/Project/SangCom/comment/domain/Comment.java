package Project.SangCom.comment.domain;

import Project.SangCom.classroom.domain.ClassPost;
import Project.SangCom.like.domain.Likes;
import Project.SangCom.post.domain.Post;
import Project.SangCom.user.domain.User;
import Project.SangCom.util.formatter.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@ToString(exclude = {"user", "post", "parent"})
public class Comment extends BaseTimeEntity {

    // 댓글 id
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    // 댓글 작성한 user id
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    // 댓글이 포함된 게시글 id
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private Post post;

    // 반 공간 게시글
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "classPost_id")
    private ClassPost classPost;

    // 부모 댓글 id
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    //== 부모 댓글을 삭제해도 자식 댓글은 남아있음 ==//
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> childList = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<Likes> likes = new ArrayList<>();

    private String author; //댓글 작성자 이름

    // 댓글 내용
    @NotNull
    @Column(columnDefinition = "TEXT")
    private String content;

    private int likeCount;

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
                   String author, String content, int likeCount, int isAnonymous, int isDeleted){
        this.user = user;
        this.post = post;
        this.parent = parent;

        this.author = author;
        this.content = content;
        this.likeCount = likeCount;

        this.isAnonymous = isAnonymous;
        this.isDeleted = isDeleted;
    }


    //== 비즈니스 코드 ==//
    public void delComment(){
        this.isDeleted = 1;
    }

    /**
     * isDeleted를 통해 논리적으로 삭제된 것들을 모아 리스트에 추가
     * @return (삭제 가능한 댓글들의 리스트) result
     */
    public List<Comment> findRemovableList(){
        List<Comment> result = new ArrayList<>();

        Optional.ofNullable(this.parent).ifPresentOrElse(
                parentComment -> { // 대댓글 삭제 시 부모댓글이 삭제되어 있는 경우
                    if(parentComment.getIsDeleted() == 1 && parentComment.isAllChildDel() == 1){
                        result.addAll(parentComment.getChildList());
                        result.add(parentComment);
                    }
                },

                () -> { // 부모댓글 삭제 시 대댓글이 없는(or 모두 지워진) 경우
                    if (isAllChildDel() == 1) {
                        result.add(this);
                        result.addAll(this.getChildList());
                    }
                }
        );
        return result;
    }

    /**
     * 대댓글 존재 여부 확인
     */
    private int isAllChildDel(){
        for (Comment child : this.childList) {
            if (child.getIsDeleted() == 0)
                return 0;
        }
        return 1;

//        return getChildList().stream()
//                .map(Comment::getIsDeleted) // 지워졌는지 여부로 바꾼다
//                .filter(isDeleted -> getIsDeleted() == 1) // 지워졌으면 1, 안지워졌으면 0 -> filter에 걸러지는 것 = 0인 것들, 있다면 0, 없다면 orElse를 통해 1 반환
//                .findAny() // 지워지지 않은게 하나라도 있다면 0을 반환 -> filter에서 걸러지지 않은 것
//                .orElse(1); // 모두 지워졌다면 1을 반환
    }

    public void updateLikes(int changeAmt)
    {
        this.likeCount += changeAmt;
    }

    //=== 연관관계 편의 메서드 ===//
    public void setUser(User user){
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

}
