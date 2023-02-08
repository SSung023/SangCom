package Project.SangCom.like.service;

import Project.SangCom.like.domain.Likes;
import Project.SangCom.like.repository.LikeRepository;
import Project.SangCom.post.domain.PostCategory;
import Project.SangCom.post.dto.PostRequest;
import Project.SangCom.post.service.PostService;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.service.UserService;
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest
@Transactional
@Slf4j
public class LikeServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private LikeRepository likeRepository;


    @Test
    @DisplayName("좋아요(Like) 객체를 id를 통해 검색할 수 있다.")
    public void findLikeById(){
        //given
        Likes likes = new Likes();

        //when
        Likes savedLike = likeRepository.save(likes);

        //then
        Assertions.assertThat(savedLike).isEqualTo(likeService.findLikesById(savedLike.getId()));
    }

    @Test
    @DisplayName("사용자는 게시글(Post)에 좋아요를 누를 수 있다.")
    public void UserCanLikePost(){
        //given
        Long saveUserId = setUserAndSave();
        Long savePostId = setPostAndSave(saveUserId);

        //when
        Long saveLikeId = likeService.likePost(saveUserId, savePostId);
        Likes likesById = likeService.findLikesById(saveLikeId);

        //then
        log.info(likesById.getPost().toString());
        log.info(likesById.getUser().toString());

        Assertions.assertThat(likesById.getUser().getId()).isEqualTo(saveUserId);
        Assertions.assertThat(likesById.getPost().getLikes()).contains(likesById);
    }

    @Test
    @DisplayName("사용자는 게시글(Post)에 좋아요를 다시 한 번 눌러서 취소할 수 있다.")
    public void UserCanUnlikePost(){
        //given
        Long saveUserId = setUserAndSave();
        Long savePostId = setPostAndSave(saveUserId);

        //when
        Long saveLikeId = likeService.likePost(saveUserId, savePostId);
        likeService.unlikePost(saveUserId, savePostId);

        //then
        Assertions.assertThatThrownBy(() -> likeService.findLikesById(saveLikeId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.DATA_ERROR_NOT_FOUND.getMessage());
    }




    private Long setUserAndSave(){
        User user = User.builder()
                .role(Role.STUDENT)
                .email("test@naver.com")
                .nickname("nickname")
                .username("username")
                .build();
        return userService.saveUser(user);
    }
    private Long setPostAndSave(Long saveUserId){
        PostRequest postRequest = PostRequest.builder()
                .authorNickname("authorNickname")
                .boardCategory(PostCategory.FREE.toString())
                .title("title1")
                .content("content1")
                .isAnonymous(0)
                .build();
        return postService.savePost(userService.findUserById(saveUserId), postRequest);
    }
}
