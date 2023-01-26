package Project.SangCom.post.controller;

import Project.SangCom.post.dto.PostRequest;
import Project.SangCom.post.dto.PostResponse;
import Project.SangCom.post.service.PostService;
import Project.SangCom.util.exception.SuccessCode;
import Project.SangCom.util.response.dto.ListResponse;
import Project.SangCom.util.response.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class PostController {
    private final PostService postService;

    /**
     * 자유게시판 전체 글 조회
     */
    @GetMapping("/board/free")
    public ResponseEntity<ListResponse<PostResponse>> getFreePostList(){


        return ResponseEntity.ok().body
                (new ListResponse<>());
    }

    /**
     * 자유게시판 특정 글 조회
     */
    @GetMapping("/board/free/{postId}")
    public ResponseEntity<SingleResponse<PostResponse>> inquiryCertainFreePost(@PathVariable Long postId){

        return ResponseEntity.ok().body(new SingleResponse<>());
    }

    /**
     * 자유게시판 특정 글 작성
     */
    @PostMapping("/board/free")
    public ResponseEntity<SingleResponse<PostResponse>> registerPost(@RequestBody PostRequest postRequest){
        Long savedPostId = postService.savePost(postRequest);
        PostResponse postResponse = postService.convertToResponse(savedPostId);

        return ResponseEntity.ok().body
                (new SingleResponse<>(SuccessCode.CREATED.getStatus(),SuccessCode.CREATED.getMessage(),postResponse));
    }

    /**
     * 자유게시판 특정 글 수정
     */
    @PatchMapping("/board/free/{postId}")
    public ResponseEntity<SingleResponse<PostResponse>> modifyPost(@PathVariable Long postId){

        return ResponseEntity.ok().body(new SingleResponse<>());
    }

    /**
     * 자유게시판 특정 글 삭제
     */
    @DeleteMapping("/board/free/{postId}")
    public ResponseEntity<SingleResponse<PostResponse>> deletePost(@PathVariable Long postId){

        return ResponseEntity.ok().body(new SingleResponse<>());
    }
}
