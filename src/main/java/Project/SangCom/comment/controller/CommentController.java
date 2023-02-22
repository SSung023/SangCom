package Project.SangCom.comment.controller;

import Project.SangCom.comment.dto.CommentRequest;
import Project.SangCom.comment.dto.CommentResponse;
import Project.SangCom.comment.service.CommentService;
import Project.SangCom.user.domain.User;
import Project.SangCom.util.exception.SuccessCode;
import Project.SangCom.util.response.dto.CommonResponse;
import Project.SangCom.util.response.dto.ListResponse;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/{category}")
@Slf4j
public class CommentController {


    private final CommentService commentService;

    /**
     * 댓글 조회
     * 댓글은 특정 게시글의 모든 댓글 조회
     */
    @GetMapping("{postId}/comment")
    public ResponseEntity<ListResponse<CommentResponse>> getCommentList (@PathVariable Long postId){

        List<CommentResponse> commentList = commentService.findPostCommentList(postId);

        return ResponseEntity.ok().body
                (new ListResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), commentList));
    }

    /**
     * 댓글 작성
     * 특정 게시글에 댓글 작성
     */
    @PostMapping("{postId}/comment")
    public ResponseEntity<SingleResponse<CommentResponse>> registerComment(@PathVariable Long postId, @RequestBody CommentRequest commentRequest){
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        commentRequest.updateAuthor(writer.getNickname());

        Long saveCommentId = commentService.saveComment(writer.getId(), postId, commentRequest);

        CommentResponse commentResponse = commentService.convertToResponse(saveCommentId);
        commentService.checkAndSetIsCommentOwner(writer, saveCommentId, commentResponse);

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
        commentRequest.updateAuthor(writer.getNickname());

        Long saveCommentId = commentService.saveReComment(writer.getId(), postId, commentId, commentRequest);

        CommentResponse commentResponse = commentService.convertToResponse(saveCommentId);
        commentService.checkAndSetIsCommentOwner(writer, saveCommentId, commentResponse);

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
