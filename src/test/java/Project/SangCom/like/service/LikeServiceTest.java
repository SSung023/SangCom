package Project.SangCom.like.service;

import Project.SangCom.like.domain.Likes;
import Project.SangCom.like.repository.LikeRepository;
import Project.SangCom.post.domain.Post;
import Project.SangCom.post.domain.PostCategory;
import Project.SangCom.post.dto.PostRequest;
import Project.SangCom.post.dto.PostResponse;
import Project.SangCom.post.service.PostService;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.service.UserService;
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ErrorCode;
import Project.SangCom.utils.WithMockCustomUser;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
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
        Long saveUserId = setUserAndSave("test@naver.com", "nickname");
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
    @DisplayName("좋아요를 누른 게시글에 대해 좋아요를 한 번 더 눌렀을 때 예외가 발생한다.")
    public void ThrowException_WhenAlreadyLiked(){
        //given
        Long saveUserId = setUserAndSave("test@naver.com", "nickname");
        Long savePostId = setPostAndSave(saveUserId);

        //when
        likeService.likePost(saveUserId, savePostId);

        //then
        Assertions.assertThatThrownBy(() -> likeService.likePost(saveUserId, savePostId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.ALREADY_LIKED.getMessage());

    }

    @Test
    @DisplayName("사용자는 게시글(Post)에 좋아요를 다시 한 번 눌러서 취소할 수 있다.")
    public void UserCanUnlikePost(){
        //given
        Long saveUserId = setUserAndSave("test@naver.com", "nickname");
        Long savePostId = setPostAndSave(saveUserId);

        //when
        Long saveLikeId = likeService.likePost(saveUserId, savePostId);
        likeService.unlikePost(saveUserId, savePostId);

        //then
        Assertions.assertThatThrownBy(() -> likeService.findLikesById(saveLikeId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.DATA_ERROR_NOT_FOUND.getMessage());
    }
    
    @Test
    @DisplayName("좋아요하지 않은 게시글에 대해 좋아요 취소를 하면 예외가 발생한다.")
    public void ThrowException_WhenNonExistLike(){
        //given
        Long saveUserId = setUserAndSave("test@naver.com", "nickname");
        Long savePostId = setPostAndSave(saveUserId);
        
        //when & then
        Assertions.assertThatThrownBy(() -> likeService.unlikePost(saveUserId, savePostId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.DATA_ERROR_NOT_FOUND.getMessage());
    }
    
    @Test
    @DisplayName("사용자가 좋아요를 누르면 게시글(Post)의 likeCount 값이 1 증가한다.")
    public void updateLikeCount(){
        //given
        Long saveUserId = setUserAndSave("test@naver.com", "nickname");
        Long savePostId = setPostAndSave(saveUserId);
        
        //when
        Long saveLikeId = likeService.likePost(saveUserId, savePostId);
        
        //then
        Assertions.assertThat(postService.findPostById(savePostId).getLikeCount()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("사용자가 좋아요 취소를 하면 게시글(Post)의 likeCount 값이 1 감소한다.")
    public void updateUnlikeCount(){
        //given
        Long saveUserId = setUserAndSave("test@naver.com", "nickname");
        Long savePostId = setPostAndSave(saveUserId);
        
        // like post
        likeService.likePost(saveUserId, savePostId);
        Assertions.assertThat(postService.findPostById(savePostId).getLikeCount()).isEqualTo(1);
        
        // unlike post
        likeService.unlikePost(saveUserId, savePostId);
        Assertions.assertThat(postService.findPostById(savePostId).getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("사용자가 게시글에 좋아요를 눌렀다면, 응답 객체의 isLikePressed의 값이 1(true)여야 한다.")
    @WithMockCustomUser
    public void isLikePressedIs1_WhenUserPressedLike(){
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long savePostId = setPostAndSave(user.getId());

        //when
        PostResponse postResponse = postService.convertToResponse(savePostId);

        likeService.likePost(user.getId(), savePostId);
        likeService.checkAndSetIsLikePressed(savePostId, postResponse);

        //then
        Assertions.assertThat(postResponse.getIsLikePressed()).isEqualTo(1);
    }

    @Test
    @DisplayName("사용자가 게시글에 좋아요를 누르지 않았다면, 응답 객체의 isLikePressed의 값이 0(false)여야 한다.")
    @WithMockCustomUser
    public void isLikePressedIs0(){
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long savePostId = setPostAndSave(user.getId());

        //when
        PostResponse postResponse = postService.convertToResponse(savePostId);
        likeService.checkAndSetIsLikePressed(savePostId, postResponse);

        //then
        Assertions.assertThat(postResponse.getIsLikePressed()).isEqualTo(0);
    }
    
    @Test
    @DisplayName("일정 시간 내에 좋아요가 제일 높은 게시글을 하나 추출할 수 있다.")
    public void getHotPost(){
        //given
        Long userId1 = setUserAndSave("test@naver.com", "nickname");
        Long userId2 = setUserAndSave("test1@naver.com", "nickname1");

        Long postId1 = setPostAndSave(userId1);
        Long postId2 = setPostAndSave(userId2);
        Long postId3 = setPostAndSave(userId1);

        //when
        likeService.likePost(userId1, postId1);
        likeService.likePost(userId2, postId1);
        likeService.likePost(userId1, postId2);
        likeService.likePost(userId1, postId3);

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));
        postService.getMostLikedPost(PostCategory.FREE, pageRequest);

        //then
        List<Post> mostLikedPost = postService.getMostLikedPost(PostCategory.FREE, pageRequest);

        Assertions.assertThat(mostLikedPost.get(0).getLikeCount()).isEqualTo(2);
        Assertions.assertThat(mostLikedPost.get(0)).isEqualTo(postService.findPostById(postId1));
    }



    
    
    




    private Long setUserAndSave(String email, String nickname){
        User user = User.builder()
                .role(Role.STUDENT)
                .email(email)
                .nickname(nickname)
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
