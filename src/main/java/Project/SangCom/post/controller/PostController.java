package Project.SangCom.post.controller;

import Project.SangCom.post.domain.Post;
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

import static Project.SangCom.post.dto.PostResponse.*;

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
     * 응답 객체에 isOwner(게시글 작성자 여부)에 대한 값 필요
     */
    @GetMapping("/board/free/{postId}")
    public ResponseEntity<SingleResponse<PostResponse>> inquiryCertainFreePost(@PathVariable Long postId){
        Post postById = postService.findPostById(postId);
        PostResponse postResponse = postService.convertToResponse(postById.getId());

        postService.checkAndSetIsPostOwner(postById.getId(), postResponse);

        return ResponseEntity.ok().body
                (new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), postResponse));
    }

    /**
     * 자유게시판 특정 글 작성
     * 응답 객체에 isOwner(게시글 작성자 여부)에 대한 값 필요
     */
    @PostMapping("/board/free")
    public ResponseEntity<SingleResponse<PostResponse>> registerPost(@RequestBody PostRequest postRequest){
        Long savedPostId = postService.savePost(postRequest);
        PostResponse postResponse = postService.convertToResponse(savedPostId);

        postService.checkAndSetIsPostOwner(savedPostId, postResponse);

        return ResponseEntity.ok().body
                (new SingleResponse<>(SuccessCode.CREATED.getStatus(),SuccessCode.CREATED.getMessage(),postResponse));
    }

    /**
     * 자유게시판 특정 글 수정
     * 응답 객체에 isOwner(게시글 작성자 여부)에 대한 값 필요
     */
    @PatchMapping("/board/free/{postId}")
    public ResponseEntity<SingleResponse<PostResponse>> modifyPost(@PathVariable Long postId, @RequestBody PostRequest postRequest){
        Long updatedPostId = postService.updatePost(postId, postRequest);
        PostResponse postResponse = postService.convertToResponse(updatedPostId);

        postService.checkAndSetIsPostOwner(updatedPostId, postResponse);

        return ResponseEntity.ok().body
                (new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), postResponse));
    }

    /**
     * 자유게시판 특정 글 삭제
     */
    @DeleteMapping("/board/free/{postId}")
    public ResponseEntity<SingleResponse<PostResponse>> deletePost(@PathVariable Long postId){

        return ResponseEntity.ok().body(new SingleResponse<>());
    }
}
