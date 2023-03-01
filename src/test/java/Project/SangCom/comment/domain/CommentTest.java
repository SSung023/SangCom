package Project.SangCom.comment.domain;

import Project.SangCom.post.domain.Post;
import Project.SangCom.post.domain.PostCategory;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
public class CommentTest {

    @Test
    @DisplayName("Comment 객체에 User 객체를 연관관계로 설정할 수 있다.")
    public void canUserEntityToCommentEntity(){
        //given - 준비
        User user1 = User.builder()
                .email("test@naver.com")
                .nickname("nickname")
                .username("username")
                .role(Role.STUDENT.getKey())
                .build();

        Comment comment = new Comment();

        //when - 실행
        comment.setUser(user1);

        //then - 검증
        Assertions.assertThat(user1).isEqualTo(comment.getUser());
        Assertions.assertThat(comment).isEqualTo(user1.getComments().get(0));
    }

    @Test
    @DisplayName("Comment 객체에 Post 객체를 연관관계로 설정할 수 있다.")
    public void canPostEntityToCommentEntity(){
        //given - 준비
        Post post1 = Post.builder()
                .author("test1")
                .category(PostCategory.valueOf("FREE"))
                .title("test title")
                .content("test connnnnnnnntent")
                .isAnonymous(0)
                .build();

        Comment comment = new Comment();

        //when - 실행
        comment.setPost(post1);

        //then - 검증
        Assertions.assertThat(post1).isEqualTo(comment.getPost());
        Assertions.assertThat(comment).isEqualTo(post1.getComments().get(0));
    }

    @Test
    @DisplayName("Comment 객체에 Parent Comment 객체를 연관관계로 설정할 수 있다.")
    public void canParentEntityToCommentEntity(){
        //given
        Comment pComment = Comment.builder()
                .author("test1")
                .content("test connnnnnnnntent")
                .isAnonymous(0)
                .build();

        Comment comment = new Comment();

        //when
        comment.setParent(pComment);

        //then
        Assertions.assertThat(pComment).isEqualTo(comment.getParent());
        Assertions.assertThat(comment).isEqualTo(pComment.getChildList().get(0));
    }

}
