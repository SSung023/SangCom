package Project.SangCom.like.domain;

import Project.SangCom.post.domain.Post;
import Project.SangCom.user.domain.User;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Like {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
}
