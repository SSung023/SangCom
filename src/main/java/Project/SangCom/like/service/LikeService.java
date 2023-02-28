package Project.SangCom.like.service;

import Project.SangCom.comment.domain.Comment;
import Project.SangCom.comment.dto.CommentResponse;
import Project.SangCom.comment.service.CommentService;
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
    private final CommentService commentService;
    private final LikeRepository likeRepository;


    /**
     * 사용자가 게시글에 좋아요를 눌렀을 때 - 좋아요 처리
     * @param saveUserId 좋아요를 누른 사용자(User)
     * @param savePostId 좋아요가 눌린 게시글(Post)
     */
    @Transactional
    public Long toggleLikePost(Long saveUserId, Long savePostId) {
        Post post = postService.findPostById(savePostId);

        // 이미 좋아요 되어있다면 좋아요 취소 처리
        Optional<Likes> foundPostLike = likeRepository.findLikes(saveUserId, savePostId);
        if (foundPostLike.isPresent()) {
            likeRepository.delete(foundPostLike.get());
            post.updateLikes(-1);
            return savePostId;
        }

        // 좋아요를 하지 않았다면 좋아요 처리
        User user = userService.findUserById(saveUserId);
        post.updateLikes(1);

        Likes likes = new Likes();
        likes.setUser(user);
        likes.setPost(post);

        Likes saveLike = likeRepository.save(likes);

        // 중간 회의 이후 PostId 보내줄 지 결정
        return saveLike.getId();
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


    //=== 댓글 좋아요 구현 ===//

    /**
     * 사용자가 댓글/대댓글에 좋아요를 눌렀을 때 - 좋아요 처리
     * @param saveUserId 좋아요를 누른 사용자
     * @param saveCommentId 좋아요가 눌린 댓글/대댓글
     * post와 comment의 연관관계는 comment 객체에서 처리 (like에서 따로 지정x)
     */
    @Transactional
    public Long toggleLikeComment(Long saveUserId, Long saveCommentId) {
        Comment comment = commentService.findCommentById(saveCommentId);

        // 이미 좋아요 되어있다면 좋아요 취소 처리
        Optional<Likes> foundCommentLike = likeRepository.findCommentLikes(saveUserId, saveCommentId);
        if (foundCommentLike.isPresent()) {
            likeRepository.delete(foundCommentLike.get());
            comment.updateLikes(-1);
            return saveCommentId;
        }

        // 좋아요가 안된 댓글이라면 좋아요 처리
        User user = userService.findUserById(saveUserId);
        comment.updateLikes(1);

        Likes likes = new Likes();
        likes.setUser(user);
        likes.setComment(comment);

        likeRepository.save(likes);
        return comment.getId();
    }

    /**
     * 이미 저장되어 있는 Like 객체를 userId와 commentId를 통해 찾은 후 반환
     * @param userId 좋아요 한 사용자의 id
     * @param commentId 좋아요 눌린 댓글의 id
     */
    public Likes findSavedCommentLike(Long userId, Long commentId){
        return likeRepository.findCommentLikes(userId, commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));
    }

    /**
     * 사용자가 좋아요를 누른 댓글인지 여부를 확인한 후, 응답 객체 필드값 설정
     * @param commentId 좋아요가 눌렸는지 확인 대상인 댓글의 PK
     * @param commentResponse isLikedPressed 필드 값을 수정할 응답 객체
     */
    public void checkAndSetIsCommentLikePressed(Long commentId, CommentResponse commentResponse){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Likes> likes = likeRepository.findCommentLikes(user.getId(), commentId);
        if (likes.isPresent()) {
            commentResponse.setIsLikePressed(1);
        }
        else {
            commentResponse.setIsLikePressed(0);
        }
    }
}
