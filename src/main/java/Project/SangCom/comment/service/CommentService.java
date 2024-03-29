package Project.SangCom.comment.service;

import Project.SangCom.comment.domain.Comment;
import Project.SangCom.comment.dto.CommentRequest;
import Project.SangCom.comment.dto.CommentResponse;
import Project.SangCom.comment.repository.CommentRepository;
import Project.SangCom.like.domain.Likes;
import Project.SangCom.like.repository.LikeRepository;
import Project.SangCom.post.domain.Post;
import Project.SangCom.post.service.PostService;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.service.UserService;
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {


    private final UserService userService;
    private final PostService postService;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;


    /**
     * 댓글 저장
     * CommentRequest(DTO)를 Entity로 변환하고 repository를 통해 저장
     * @param commentRequest 사용자에게 받은 댓글 정보
     */
    @Transactional
    public Long saveComment(Long writerId, Long postId, CommentRequest commentRequest){
        User user = userService.findUserById(writerId);
        Post post = postService.findPostById(postId);
        Comment comment = commentRequest.toEntity();

        Comment savedComment = commentRepository.save(comment);

        post.updateCommentCnt(1);

        savedComment.setUser(user);
        savedComment.setPost(post);

        return savedComment.getId();
    }

    /**
     * 대댓글 저장
     */
    @Transactional
    public Long saveReComment(Long writerId, Long postId, Long pCommentId, CommentRequest commentRequest){
        User user = userService.findUserById(writerId);
        Post post = postService.findPostById(postId);
        Comment pComment = findCommentById(pCommentId);
        Comment comment = commentRequest.toEntity();

        Comment savedComment = commentRepository.save(comment);

        post.updateCommentCnt(1);

        savedComment.setUser(user);
        savedComment.setParent(pComment);

        return savedComment.getId();
    }

    /**
     * 댓글 id를 통해 댓글 객체를 찾아서 반환
     * @param commentId 찾고자 하는 댓글 id
     */
    public Comment findCommentById(Long commentId){
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));
    }

    /**
     * 모든 댓글 리스트로 반환
     */
    public List<Comment> findAll(){
        return commentRepository.findAll();
    }

    /**
     * commentId에 해당하는 댓글 삭제 (isDeleted = 1(true)로 변경)
     * @param commentId 삭제할 댓글 id (PK)
     */
    @Transactional
    public Long deleteComment(Long commentId, Long postId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));
        Post post = postService.findPostById(postId);

        post.updateCommentCnt(-1);
        comment.delComment();

        return comment.getId();
    }

    public int checkIsCommentOwner(User user, Comment comment){
        if (Objects.equals(comment.getUser().getId(), user.getId())){
            return 1;
        }
        else {
            return 0;
        }
    }

    /**
     * 특정 게시글에서 조건에 맞는 댓글들을 반환
     * @param postId 댓글 리스트를 받아오고 싶은 게시글의 Id
     */
    public List<CommentResponse> findPostCommentList(User user, Long postId){
        List<CommentResponse> result = new ArrayList<>();

        List<Comment> commentList = commentRepository.findPostComment(postId);
        for (Comment target : commentList) {
            if (isProperComment(target)){ // 조건에 맞는다면 리스트에 포함
                result.add(convertToResponse(user, target));
            }
        }
        return result;
    }

    /**
     * 특정 댓글(부모 댓글 대상)이 반환 대상인지 확인하는 메서드
     * @param comment 반환 대상 포함 대상인지 확인하는 Comment 객체
     */
    private boolean isProperComment(Comment comment){
        if (comment.getIsDeleted() == 1){ // 해당 댓글이 삭제 처리되어 있다면
            List<Comment> childList = comment.getChildList();

            for (Comment child : childList) { // 대댓글 중 삭제되지 않은 것이 하나라도 있으면 true
                if (child.getIsDeleted() == 0)  return true;
            }
            // 대댓글이 없거나, 모두 삭제 처리되어있다면 false
            return false;
        }

        // 해당 댓글이 삭제 처리되어 있지 않다면 true
        return true;
    }





    /**
     * 특정 게시글에 대한 댓글에 전달할 CommentResponse(Comment)객체로 반환
     * @param commentId CommentResponse로 변환하고 싶은 comment의 PK
     */
    public CommentResponse convertToResponse(User user, Long commentId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));

        return CommentResponse.builder()
                .id(comment.getId())
                .authorName(checkAuthorName(comment))
                .content(checkIsDeleted(comment))
                .likeCount(comment.getLikeCount())
                .isAnonymous(comment.getIsAnonymous())
                .isDeleted(comment.getIsDeleted())
                .isOwner(checkIsCommentOwner(user, comment))
                .isLikePressed(checkIsLikePressed(user, comment))
                .createdDate(comment.getCreatedDate())
                .childComment(checkChildList(user, comment))
                .build();
    }
    public CommentResponse convertToResponse(User user, Comment comment){
        return CommentResponse.builder()
                .id(comment.getId())
                .authorName(checkAuthorName(comment))
                .content(checkIsDeleted(comment))
                .likeCount(comment.getLikeCount())
                .isAnonymous(comment.getIsAnonymous())
                .isDeleted(comment.getIsDeleted())
                .isOwner(checkIsCommentOwner(user, comment))
                .isLikePressed(checkIsLikePressed(user, comment))
                .createdDate(comment.getCreatedDate())
                .childComment(checkChildList(user, comment))
                .build();
    }
    private CommentResponse convertToSingleResponse(User user, Comment comment){
        return CommentResponse.builder()
                .id(comment.getId())
                .authorName(checkAuthorName(comment))
                .content(checkIsDeleted(comment))
                .likeCount(comment.getLikeCount())
                .isAnonymous(comment.getIsAnonymous())
                .isDeleted(comment.getIsDeleted())
                .isOwner(checkIsCommentOwner(user, comment))
                .isLikePressed(checkIsLikePressed(user, comment))
                .createdDate(comment.getCreatedDate())
                .build();
    }

    private int checkIsLikePressed(User user, Comment comment) {
        Optional<Likes> likes = likeRepository.findCommentLikes(user.getId(), comment.getId());
        if (likes.isPresent()) {
            return 1;
        }
        else {
            return 0;
        }
    }
    private String checkAuthorName(Comment comment){
        if (comment.getIsDeleted() == 1){
            return "알 수 없음";
        }
        if (comment.getIsAnonymous() == 0){
            return comment.getAuthor();
        }
        else {
            return "익명";
        }
    }
    private String checkIsDeleted(Comment comment){
        if(comment.getIsDeleted() == 0)
            return comment.getContent();
        else
            return "댓글이 삭제되었습니다.";
    }
    /**
     * 특정 댓글의 대댓글이 있는 경우 대댓글들을 CommentResponse 리스트로 변환
     */
    private List<CommentResponse> checkChildList(User user, Comment comment){
        return comment.getChildList().stream()
                .map(c -> convertToSingleResponse(user, c))
                .collect(Collectors.toList());
    }
}
