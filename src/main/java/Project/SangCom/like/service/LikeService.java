package Project.SangCom.like.service;

import Project.SangCom.like.domain.Likes;
import Project.SangCom.like.repository.LikeRepository;
import Project.SangCom.post.domain.Post;
import Project.SangCom.post.dto.PostResponse;
import Project.SangCom.post.service.PostService;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.service.UserService;
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class LikeService {
    private final UserService userService;
    private final PostService postService;
    private final LikeRepository likeRepository;


    /**
     * 사용자가 게시글에 좋아요를 눌렀을 때 - 좋아요 처리
     * @param saveUserId 좋아요를 누른 사용자(User)
     * @param savePostId 좋아요가 눌린 게시글(Post)
     */
    @Transactional
    public Long likePost(Long saveUserId, Long savePostId) {
        // 이미 좋아요 되어있다면 예외 처리
        if (likeRepository.findLikes(saveUserId, savePostId).isPresent()) {
            throw new BusinessException(ErrorCode.ALREADY_LIKED);
        }

        User user = userService.findUserById(saveUserId);
        Post post = postService.findPostById(savePostId);
        post.updateLikes(1);

        Likes likes = new Likes();
        likes.setUser(user);
        likes.setPost(post);

        Likes saveLike = likeRepository.save(likes);
        return saveLike.getId();
    }

    /**
     * 사용자가 좋아요한 게시글에 좋아요 버튼을 눌렀을 때 - 좋아요 취소 처리
     * 중간 과정에서 유효하지 않은 과정이 있다면 BusinessException 발생
     * @param saveUserId 좋아요를 누른 사용자
     * @param savePostId 좋아요가 눌린 게시글
     */
    @Transactional
    public void unlikePost(Long saveUserId, Long savePostId) {
        Post post = postService.findPostById(savePostId);
        post.updateLikes(-1);

        Likes savedLike = findSavedLike(saveUserId, savePostId);
        likeRepository.delete(savedLike);
    }


    /**
     * like_id(PK)를 통해 Like 객체를 찾아서 반환
     * @param likeId 찾고자 하는 좋아요(Like) 객체의 Id
     */
    public Likes findLikesById(Long likeId){
        return likeRepository.findById(likeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));
    }

    /**
     * 이미 저장되어 있는 Like 객체를 userId와 postId를 통해 찾은 후 반환
     * @param userId 좋아요 한 사용자의 id
     * @param postId 좋아요 한 게시글의 id
     */
    public Likes findSavedLike(Long userId, Long postId){
        return likeRepository.findLikes(userId, postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));
    }


    /**
     * 사용자가 좋아요를 누른 게시글인지 여부를 확인한 후, 응답 객체 필드값 설정
     * @param postId 좋아요가 눌렸는지 확인 대상인 게시글의 PK
     * @param postResponse isLikedPressed 필드 값을 수정할 응답 객체
     */
    public void checkAndSetIsLikePressed(Long postId, PostResponse postResponse){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Likes> likes = likeRepository.findLikes(user.getId(), postId);
        if (likes.isPresent()) {
            postResponse.setIsLikePressed(1);
        }
        else {
            postResponse.setIsLikePressed(0);
        }
    }
}
