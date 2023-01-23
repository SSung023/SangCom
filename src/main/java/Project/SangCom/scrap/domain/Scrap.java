package Project.SangCom.scrap.domain;

import Project.SangCom.post.domain.Post;
import Project.SangCom.user.domain.User;
import lombok.Getter;
import org.springframework.security.core.parameters.P;

import javax.persistence.*;

@Entity
@Getter
public class Scrap {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scrap_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    //=== 연관관계 편의 메서드 ===//
    public void setPost(Post post){
        this.post = post;
        post.getScraps().add(this);
    }
    public void setUser(User user){
        this.user = user;
        user.getScraps().add(this);
    }
}
