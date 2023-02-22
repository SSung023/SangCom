package Project.SangCom.comment.service;

import Project.SangCom.comment.domain.Comment;
import Project.SangCom.comment.dto.CommentRequest;
import Project.SangCom.comment.dto.CommentResponse;
import Project.SangCom.comment.repository.CommentRepository;
import Project.SangCom.post.domain.Post;
import Project.SangCom.post.service.PostService;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.service.UserService;
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

import java.util.List;

import static Project.SangCom.post.dto.PostResponse.FALSE;
import static Project.SangCom.post.dto.PostResponse.TRUE;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final UserService userService;
    private final PostService postService;
    //private final CommentService commentService;

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

        savedComment.setUser(user);
//        savedComment.setPost(post); // 대댓글은 Post 밑에 바로 달리는 것이 아니기 때문에 주석처리
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
    public Long deleteComment(Long commentId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));
        comment.delComment();

        // 조건에 맞지 않으면 빈 리스트 반환 -> DB에 있는 것 삭제X
        List<Comment> removableCommentList = comment.findRemovableList();
        commentRepository.deleteAll(removableCommentList);

        return comment.getId();
    }

    /**
     * commentId에 저장되어 있는 nickname과 사용자의 nickname이 일치하는지 확인
     * 작성자가 맞으므로 commentResponse 객체의 isOwner를 TRUE(1)로 설정,
     * 작성자가 아니라면 commentRepsonse 객체의 isOwner를 FALSE(0)으로 설정
     */
    public void checkAndSetIsCommentOwner(Long commentId, CommentResponse commentResponse){
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));

        if (comment.getAuthor().equals(principal.getNickname())){
            commentResponse.setIsOwner(TRUE);
        }
        else {
            commentResponse.setIsOwner(FALSE);
        }
    }

    /**
     * 댓글 조회
     * 아무리 봐도 postId에 따라 다른 댓글들을 get하게 해줘야할거 같은데..
     * -> 인자로 postId 받고 넘기자
     * @param pageable
     */
    public Slice<CommentResponse> getNotDeletedCommentList(Long postId, Pageable pageable){
        Slice<Comment> comments = commentRepository.findAllByIsDeletedAndPostId(0, postId, pageable);
        Slice<CommentResponse> commentResponses
                = comments.map(c -> new CommentResponse(c.getId(), c.getAuthor(),
                c.getContent(), c.getLikeCount(), 0, 0, c.getIsAnonymous()));

        return commentResponses;
    }

    /**
     * 특정 게시글에 대한 댓글에 전달할 CommentResponse(Comment)객체로 반환
     * @param commentId CommentResponse로 변환하고 싶은 comment의 PK
     *
     * 특정 게시글을 구분하려면 postId 필요하지 않나?
     */
    public CommentResponse convertToResponse(Long commentId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));

        return CommentResponse.builder()
                .id(comment.getId())
                .authorName(checkIsAnonymous(comment))
                .content(checkIsDeleted(comment))
                .likeCount(comment.getLikeCount())
                .isAnonymous(comment.getIsAnonymous())
                .build();
    }

    public CommentResponse convertToResponse(Comment comment){
        return CommentResponse.builder()
                .id(comment.getId())
                .authorName(checkIsAnonymous(comment))
                .content(checkIsDeleted(comment))
                .likeCount(comment.getLikeCount())
                .isAnonymous(comment.getIsAnonymous())
                .build();
    }
    private String checkIsAnonymous(Comment comment){
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

}
