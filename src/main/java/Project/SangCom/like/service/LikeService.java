package Project.SangCom.like.service;

import Project.SangCom.like.domain.Likes;
import Project.SangCom.like.repository.LikeRepository;
import Project.SangCom.post.domain.Post;
import Project.SangCom.post.service.PostService;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.service.UserService;
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        User user = userService.findUserById(saveUserId);
        Post post = postService.findPostById(savePostId);

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
    public void unlikePost(Long saveUserId, Long savePostId) {
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
}
