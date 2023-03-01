package Project.SangCom.post.domain;


import Project.SangCom.comment.domain.Comment;
import Project.SangCom.like.domain.Likes;
import Project.SangCom.scrap.domain.Scrap;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
//@Transactional
class PostTest {
    
    @Test
    @DisplayName("Post 객체에 User 객체를 연관관계로 설정할 수 있다.")
    public void canUserEntityToPostEntity(){
        //given
        User user1 = User.builder()
                .email("test@naver.com")
                .nickname("nickname")
                .username("username")
                .role(Role.STUDENT.getKey())
                .build();

        Post post = new Post();
        
        //when
        post.addUser(user1);
        
        //then
        Assertions.assertThat(user1).isEqualTo(post.getUser());
        Assertions.assertThat(post).isEqualTo(user1.getPosts().get(0));
    }
    
    @Test
    @DisplayName("Post에 Comment, Likes, Scrap을 연관관계로 설정할 수 있다.")
    public void canCreateBoardEntity(){
        //given
        Post post = new Post();
        Comment comment = new Comment();
        Likes likes = new Likes();
        Scrap scrap = new Scrap();
        
        //when
        comment.setPost(post);
        likes.setPost(post);
        scrap.setPost(post);

        
        //then
        log.info("comment에 설정된 post: " + comment.getPost());
        log.info("post: " + post);
        Assertions.assertThat(post).isEqualTo(comment.getPost());
        Assertions.assertThat(post).isEqualTo(likes.getPost());
        Assertions.assertThat(post).isEqualTo(scrap.getPost());
    }

}