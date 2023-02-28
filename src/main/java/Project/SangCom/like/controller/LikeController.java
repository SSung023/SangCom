package Project.SangCom.like.controller;

import Project.SangCom.comment.dto.CommentResponse;
import Project.SangCom.comment.service.CommentService;
import Project.SangCom.like.dto.LikeDTO;
import Project.SangCom.like.service.LikeService;
import Project.SangCom.post.dto.PostResponse;
import Project.SangCom.post.service.PostService;
import Project.SangCom.user.domain.User;
import Project.SangCom.util.exception.SuccessCode;
import Project.SangCom.util.response.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like")
@Slf4j
public class LikeController {
    private final LikeService likeService;
    private final PostService postService;
    private final CommentService commentService;

    @PostMapping("/board")
    public ResponseEntity<SingleResponse<PostResponse>> likePost(@RequestBody LikeDTO likeDTO){
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        likeService.toggleLikePost(writer.getId(), likeDTO.getPostId());

        PostResponse postResponse = postService.convertToDetailResponse(writer, likeDTO.getPostId());

        return ResponseEntity.ok().body
                (new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), postResponse));
    }


    //=== 댓글 구현 ===//
    @PostMapping("/board/comment")
    public ResponseEntity<SingleResponse<CommentResponse>> likeComment(@RequestBody LikeDTO likeDTO){
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long commentId = likeService.toggleLikeComment(writer.getId(), likeDTO.getCommentId());

        CommentResponse commentResponse = commentService.convertToResponse(writer, commentId);

        return ResponseEntity.ok().body
                (new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), commentResponse));
    }

}
