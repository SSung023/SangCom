package Project.SangCom.comment.controller;

import Project.SangCom.comment.domain.Comment;
import Project.SangCom.comment.dto.CommentRequest;
import Project.SangCom.comment.dto.CommentResponse;
import Project.SangCom.comment.service.CommentService;
import Project.SangCom.post.domain.Post;
import Project.SangCom.post.repository.PostRepository;
import Project.SangCom.post.service.PostService;
import Project.SangCom.user.domain.User;
import Project.SangCom.util.exception.SuccessCode;
import Project.SangCom.util.response.dto.CommonResponse;
import Project.SangCom.util.response.dto.PagingResponse;
import Project.SangCom.util.response.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/free")
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;

    /**
     * 댓글 조회
     * 댓글은 특정 게시글의 모든 댓글 조회 (페이징 처리 필수)
     * 특정 댓글만 조회는 불가능 -> 마이페이지 때문에 만들어야하나..?
     */
    @GetMapping("{postId}/comment")
    public ResponseEntity<PagingResponse<CommentResponse>> getCommentList
    (@PathVariable Long postId, @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
        Slice<CommentResponse> commentList = commentService.getNotDeletedCommentList(postId, pageable);

        return ResponseEntity.ok().body
                (new PagingResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), commentList));
    }

    /**
     * 댓글 작성
     * 특정 게시글에 댓글 작성
     */
     @PostMapping("{postId}/comment")
    public ResponseEntity<SingleResponse<CommentResponse>> registerComment(@PathVariable Long postId, @RequestBody CommentRequest commentRequest){
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Post post = postService.findPostById(postId);

        Long saveCommentId = commentService.saveComment(writer, post, commentRequest);
        CommentResponse commentResponse = commentService.convertToResponse(saveCommentId);

        return ResponseEntity.ok().body
                (new SingleResponse<>(SuccessCode.CREATED.getStatus(), SuccessCode.CREATED.getMessage(), commentResponse));
    }

    /**
     * 대댓글 작성
     * 특정 댓글에 대댓글 작성
     */
    @PostMapping("{postId}/comment/{commentId}")
    public ResponseEntity<SingleResponse<CommentResponse>> registerReComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestBody CommentRequest commentRequest){
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Post post = postService.findPostById(postId);
        Comment comment = commentService.findCommentById(commentId);

        Long saveCommentId = commentService.saveReComment(writer, post, comment, commentRequest);
        CommentResponse commentResponse = commentService.convertToResponse(saveCommentId);

        return ResponseEntity.ok().body
                (new SingleResponse<>(SuccessCode.CREATED.getStatus(), SuccessCode.CREATED.getMessage(), commentResponse));
    }

    /**
     * 댓글 삭제
     * 특정 게시글의 특정 댓글 삭제
     */
    @DeleteMapping("{postId}/comment/{commentId}")
    public ResponseEntity<CommonResponse> deleteComment(@PathVariable Long postId, @PathVariable Long commentId){
        commentService.deleteComment(commentId);

        return ResponseEntity.ok().body
                (new CommonResponse(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage()));
    }
}
