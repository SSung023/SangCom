package Project.SangCom.like.repository;

import Project.SangCom.like.domain.Likes;
import Project.SangCom.post.domain.Post;
import Project.SangCom.post.domain.PostCategory;
import Project.SangCom.post.dto.PostRequest;
import Project.SangCom.post.repository.PostRepository;
import Project.SangCom.post.service.PostService;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.repository.UserRepository;
import Project.SangCom.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
class LikeRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private LikeRepository likeRepository;

    @Test
    @DisplayName("Like 객체를 save하고 찾을 수 있다.")
    public void likeTest(){
        //given
        Likes likes = new Likes();

        //when
        Likes savedLike = likeRepository.save(likes);

        //then
        Assertions.assertThat(savedLike).isEqualTo(likeRepository.findById(savedLike.getId()).get());
    }

    @Test
    @DisplayName("userId와 postId를 통해 Likes 객체를 찾을 수 있다.")
    public void findLike(){
        //given
        Long userId = setUserAndSave();
        Long postId = setPostAndSave(userId);

        //when
        Likes likes = new Likes();
        likes.setUser(userRepository.findById(userId).get());
        likes.setPost(postRepository.findById(postId).get());

        likeRepository.save(likes);
        Likes foundLike = likeRepository.findLikes(userId, postId).get();

        //then
        Assertions.assertThat(foundLike.getUser().getId()).isEqualTo(userId);
        Assertions.assertThat(foundLike.getPost().getId()).isEqualTo(postId);
    }

    @Test
    @DisplayName("저장되어 있는 Likes 객체를 삭제할 수 있다.")
    public void deleteLike(){
        //given
        Long userId = setUserAndSave();
        Long postId = setPostAndSave(userId);

        //when
        Likes likes = new Likes();
        likes.setUser(userRepository.findById(userId).get());
        likes.setPost(postRepository.findById(postId).get());

        Likes savedLike = likeRepository.save(likes);
        likeRepository.delete(savedLike);

        //then
        Assertions.assertThat(likeRepository.findById(savedLike.getId())).isEqualTo(Optional.empty());
    }





    private Long setUserAndSave(){
        User user = User.builder()
                .role(Role.STUDENT)
                .email("test@naver.com")
                .nickname("nickname")
                .username("username")
                .build();
        User save = userRepository.save(user);
        return save.getId();
    }
    private Long setPostAndSave(Long saveUserId){
        Post post = Post.builder()
                .category(PostCategory.FREE)
                .author("author")
                .title("title")
                .content("content")
                .build();
        post.addUser(userRepository.findById(saveUserId).get());
        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }
}