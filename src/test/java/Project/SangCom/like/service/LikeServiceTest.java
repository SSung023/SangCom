package Project.SangCom.like.service;

import Project.SangCom.comment.dto.CommentRequest;
import Project.SangCom.comment.dto.CommentResponse;
import Project.SangCom.comment.service.CommentService;
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

import java.util.List;

@SpringBootTest
@Transactional
@Slf4j
public class LikeServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
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
        Long saveLikeId = likeService.toggleLikePost(saveUserId, savePostId);
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
        Long saveUserId = setUserAndSave("test@naver.com", "nickname");
        Long savePostId = setPostAndSave(saveUserId);

        //when
        Long saveLikeId = likeService.toggleLikePost(saveUserId, savePostId);
        likeService.toggleLikePost(saveUserId, savePostId);

        //then
        Assertions.assertThatThrownBy(() -> likeService.findLikesById(saveLikeId))
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
        Long saveLikeId = likeService.toggleLikePost(saveUserId, savePostId);
        
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
        likeService.toggleLikePost(saveUserId, savePostId);
        Assertions.assertThat(postService.findPostById(savePostId).getLikeCount()).isEqualTo(1);
        
        // unlike post
        likeService.toggleLikePost(saveUserId, savePostId);
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
        PostResponse postResponse = postService.convertToPreviewResponse(user, savePostId);

        likeService.toggleLikePost(user.getId(), savePostId);
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
        PostResponse postResponse = postService.convertToPreviewResponse(user, savePostId);
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
        likeService.toggleLikePost(userId1, postId1);
        likeService.toggleLikePost(userId2, postId1);
        likeService.toggleLikePost(userId1, postId2);
        likeService.toggleLikePost(userId1, postId3);

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));
        postService.getMostLikedPost(PostCategory.FREE, pageRequest);

        //then
        List<Post> mostLikedPost = postService.getMostLikedPost(PostCategory.FREE, pageRequest);

        Assertions.assertThat(mostLikedPost.get(0).getLikeCount()).isEqualTo(2);
        Assertions.assertThat(mostLikedPost.get(0)).isEqualTo(postService.findPostById(postId1));
    }


    //=== 댓글 테스트 코드 ===//
    @Test
    @DisplayName("사용자는 댓글(Comment)에 좋아요를 누를 수 있다.")
    public void UserCanLikeComment(){
        //given
        Long saveUserId = setUserAndSave("test@naver.com", "nickname");
        Long savePostId = setPostAndSave(saveUserId);
        Long saveCommentId = setCommentAndSave(saveUserId, savePostId);

        //when
        Long commentId = likeService.toggleLikeComment(saveUserId, saveCommentId);
    }

    @Test
    @DisplayName("사용자가 좋아요를 누르면 댓글(Comment)의 likeCount 값이 1 증가한다.")
    public void updateCommentLikeCount(){
        //given
        Long saveUserId = setUserAndSave("test@naver.com", "nickname");
        Long savePostId = setPostAndSave(saveUserId);
        Long saveCommentId = setCommentAndSave(saveUserId, savePostId);

        //when
        Long saveLikeId = likeService.toggleLikeComment(saveUserId, saveCommentId);

        //then
        Assertions.assertThat(commentService.findCommentById(saveCommentId).getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("사용자가 좋아요 취소를 하면 댓글(Comment)의 likeCount 값이 1 감소한다.")
    public void updateCommentUnlikeCount(){
        //given
        Long saveUserId = setUserAndSave("test@naver.com", "nickname");
        Long savePostId = setPostAndSave(saveUserId);
        Long saveCommentId = setCommentAndSave(saveUserId, savePostId);

        // like comment
        likeService.toggleLikeComment(saveUserId, saveCommentId);
        Assertions.assertThat(commentService.findCommentById(saveCommentId).getLikeCount()).isEqualTo(1);

        // unlike comment
        likeService.toggleLikeComment(saveUserId, saveCommentId);
        Assertions.assertThat(commentService.findCommentById(saveCommentId).getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("사용자가 댓글에 좋아요를 눌렀다면, 응답 객체의 isLikePressed의 값이 1(true)여야 한다.")
    @WithMockCustomUser
    public void isCommentLikePressedIs1_WhenUserPressedLike(){
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long savePostId = setPostAndSave(user.getId());
        Long saveCommentId = setCommentAndSave(user.getId(), savePostId);

        //when
        CommentResponse commentResponse = commentService.convertToResponse(user, saveCommentId);

        likeService.toggleLikeComment(user.getId(), saveCommentId);
        likeService.checkAndSetIsCommentLikePressed(saveCommentId, commentResponse);

        //then
        Assertions.assertThat(commentResponse.getIsLikePressed()).isEqualTo(1);
    }

    @Test
    @DisplayName("사용자가 댓글에 좋아요를 누르지 않았다면, 응답 객체의 isLikePressed의 값이 0(false)여야 한다.")
    @WithMockCustomUser
    public void isCommentLikePressedIs0(){
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long savePostId = setPostAndSave(user.getId());
        Long saveCommentId = setCommentAndSave(user.getId(), savePostId);

        //when
        CommentResponse commentResponse = commentService.convertToResponse(user, saveCommentId);
        likeService.checkAndSetIsCommentLikePressed(saveCommentId, commentResponse);

        //then
        Assertions.assertThat(commentResponse.getIsLikePressed()).isEqualTo(0);
    }

    
    
    



    private Long setUserAndSave(String email, String nickname){
        User user = User.builder()
                .role(Role.STUDENT.getKey())
                .email(email)
                .nickname(nickname)
                .username("username")
                .build();
        return userService.saveUser(user);
    }
    private Long setPostAndSave(Long saveUserId){
        PostRequest postRequest = PostRequest.builder()
                .authorNickname("authorNickname")
                .title("title1")
                .content("content1")
                .isAnonymous(0)
                .build();
        return postService.savePost(saveUserId, PostCategory.FREE, postRequest);
    }

    private Long setCommentAndSave(Long saveUserId, Long savePostId){
        CommentRequest commentRequest = CommentRequest.builder()
                .authorName("author")
                .content("comment content")
                .isAnonymous(0)
                .build();
        return commentService.saveComment(saveUserId, savePostId, commentRequest);
    }
}
