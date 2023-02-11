package Project.SangCom.comment.service;

import Project.SangCom.comment.domain.Comment;
import Project.SangCom.comment.dto.CommentRequest;
import Project.SangCom.comment.dto.CommentResponse;
import Project.SangCom.comment.repository.CommentRepository;
import Project.SangCom.post.domain.Post;
import Project.SangCom.post.domain.PostCategory;
import Project.SangCom.post.dto.PostResponse;
import Project.SangCom.post.repository.PostRepository;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.repository.UserRepository;
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
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
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    /**
     * 댓글 저장
     * CommentRequest(DTO)를 Entity로 변환하고 repository를 통해 저장
     * @param commentRequest 사용자에게 받은 댓글 정보
     */
    @Transactional
    public Long saveComment(User writer, Post post, CommentRequest commentRequest){
        Comment comment = commentRequest.toEntity();
        comment.addUser(writer);
        comment.setPost(post);

        Comment saveComment = commentRepository.save(comment);

        return saveComment.getId();
    }

    /**
     * 대댓글 저장
     */
    @Transactional
    public Long saveReComment(User writer, Post post, Comment pComment, CommentRequest commentRequest){
        Comment comment = commentRequest.toEntity();
        comment.addUser(writer);
        comment.setPost(post);
        comment.setParent(pComment);

        Comment saveComment = commentRepository.save(comment);

        return saveComment.getId();
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
     * commentId에 해당하는 댓글 삭제 (isDeleted = 1(true)로 변경)
     * @param commentId 삭제할 댓글 id (PK)
     */
    @Transactional
    public Long deleteComment(Long commentId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));
        comment.delComment();

        // DB에는 남아있지만 (isDeleted에 의해) 논리적으로 삭제된 댓글 리스트 (한 댓글의 부모-자식에 대해서)
        List<Comment> removeableCommentList = comment.findRemoveableList();
        commentRepository.deleteAll(removeableCommentList);

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
     * 아무리 봐도 postId에 따라 다른 댓글들을 get하게 해줘야할거 같은데.. 흠..
     * @param pageable
     */
    public Slice<CommentResponse> getNotDeletedCommentList(Long postId, Pageable pageable){
        Slice<Comment> comments = commentRepository.findAllByIsDeletedAndPostId(0, postId, pageable);
        Slice<CommentResponse> commentResponses
                = comments.map(c -> new CommentResponse(c.getId(), c.getAuthor(),
                c.getDate(), c.getContent(),0, c.getIsAnonymous()));

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
                .authorName(comment.getAuthor())
                .date(comment.getDate())
                .content(comment.getContent())
                .isAnonymous(comment.getIsAnonymous())
                .build();
    }

}
