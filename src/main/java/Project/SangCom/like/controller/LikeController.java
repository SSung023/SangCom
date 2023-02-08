package Project.SangCom.like.controller;

import Project.SangCom.like.dto.LikeDTO;
import Project.SangCom.like.service.LikeService;
import Project.SangCom.user.domain.User;
import Project.SangCom.util.exception.SuccessCode;
import Project.SangCom.util.response.dto.CommonResponse;
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

    @PostMapping("/board")
    public ResponseEntity<SingleResponse<LikeDTO>> likePost(@RequestBody LikeDTO likeDTO){
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        likeService.likePost(writer.getId(), likeDTO.getPostId());

        return ResponseEntity.ok().body
                (new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), likeDTO));
    }

    @DeleteMapping("/board")
    public ResponseEntity<SingleResponse<LikeDTO>> unlikePost(@RequestBody LikeDTO likeDTO){
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        likeService.unlikePost(writer.getId(), likeDTO.getPostId());

        return ResponseEntity.ok().body
                (new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), likeDTO));
    }
}
