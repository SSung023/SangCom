package Project.SangCom.post.controller;

import Project.SangCom.like.service.LikeService;
import Project.SangCom.post.domain.Post;
import Project.SangCom.post.domain.PostCategory;
import Project.SangCom.post.dto.PostRequest;
import Project.SangCom.post.dto.PostResponse;
import Project.SangCom.post.service.PostService;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.domain.embedded.StudentInfo;
import Project.SangCom.user.repository.UserRepository;
import Project.SangCom.user.service.UserService;
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

import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class PostController {
    private final PostService postService;
    private final LikeService likeService;

    //for test
    private final UserService userService;
    private final UserRepository userRepository;


    /**
     * 자유게시판의 실시간 인기글 조회
     */
    @GetMapping("/board/free/best")
    public ResponseEntity<SingleResponse<PostResponse>> getFreeBoardInfo
        (@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Post> mostLikedPost = postService.getMostLikedPost(PostCategory.FREE, pageable);
        PostResponse postResponse = null;
        if (!mostLikedPost.isEmpty()){
            postResponse = postService.convertToPreviewResponse(user, mostLikedPost.get(0));
        }

        return ResponseEntity.ok().body
                (new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), postResponse));
    }

    /**
     * 자유게시판 전체 글 조회
     * ex) /board/free/list?page=0&size=10
     */
    @GetMapping("/board/free/list")
    public ResponseEntity<PagingResponse<PostResponse>> getFreePostList
        (@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Slice<PostResponse> postList = postService.getNotDeletedPostList(user, PostCategory.FREE, pageable);

        return ResponseEntity.ok().body
                (new PagingResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(),postList));
    }

    /**
     * 자유게시판 게시글 검색 - 제목만, 내용만, 제목+내용
     * ex) /api/board/free/search?page=0&size=3
     */
    @GetMapping("/board/free/search")
    public ResponseEntity<PagingResponse<PostResponse>> searchPosts
        (@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
         @RequestParam String query, @RequestParam String keyword){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Slice<PostResponse> postList = postService.searchPosts(user, query, keyword, PostCategory.FREE, pageable);
        return ResponseEntity.ok().body
                (new PagingResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(),postList));
    }

    /**
     * 자유게시판 특정 글 조회
     * 응답 객체에 isOwner(게시글 작성자 여부)에 대한 값 필요
     * 응답 객체에 isLikePressed(좋아요 선택 여부)에 대한 값 필요
     */
    @GetMapping("/board/free/{postId}")
    public ResponseEntity<SingleResponse<PostResponse>> inquiryCertainFreePost(@PathVariable Long postId){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Post postById = postService.findPostById(postId);
        PostResponse postDetailResponse = postService.convertToDetailResponse(user, postById);

        return ResponseEntity.ok().body
                (new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), postDetailResponse));
    }

    /**
     * 자유게시판 특정 글 작성
     * 응답 객체에 isOwner(게시글 작성자 여부)에 대한 값 필요
     */
    @PostMapping("/board/free")
    public ResponseEntity<SingleResponse<PostResponse>> registerPost(@RequestBody PostRequest postRequest){
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        postRequest.updateAuthor(writer.getNickname());

        Long savedPostId = postService.savePost(writer.getId(), postRequest);

        PostResponse postDetailResponse = postService.convertToDetailResponse(writer, savedPostId);

        return ResponseEntity.ok().body
                (new SingleResponse<>(SuccessCode.CREATED.getStatus(),SuccessCode.CREATED.getMessage(), postDetailResponse));
    }

    /**
     * 자유게시판 특정 글 수정
     * 응답 객체에 isOwner(게시글 작성자 여부)에 대한 값 필요
     */
    @PatchMapping("/board/free/{postId}")
    public ResponseEntity<SingleResponse<PostResponse>> modifyPost(@PathVariable Long postId, @RequestBody PostRequest postRequest){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long updatedPostId = postService.updatePost(postId, postRequest);
        PostResponse postDetailResponse = postService.convertToDetailResponse(user, updatedPostId);

        return ResponseEntity.ok().body
                (new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), postDetailResponse));
    }

    /**
     * 자유게시판 특정 글 삭제
     */
    @DeleteMapping("/board/free/{postId}")
    public ResponseEntity<CommonResponse> deletePost(@PathVariable Long postId){
        postService.deletePost(postId);

        return ResponseEntity.ok().body
                (new CommonResponse(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage()));
    }




    //=== 테스트용 컨트롤러 ===//
    @GetMapping("/board/test")
    public ResponseEntity<CommonResponse> testPost(){
        // User 객체 생성
        User mine = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<User> byEmail = userRepository.findByEmail("test@naver.com");
        User savedUser;
        if (byEmail.isEmpty()){
            User user = User.builder()
                    .role(Role.STUDENT)
                    .email("test@naver.com")
                    .nickname("닉네임")
                    .username("이름이름")
                    .studentInfo(new StudentInfo("1", "4", "2"))
                    .build();
            Long userId = userService.saveUser(user);
            savedUser = userService.findUserById(userId);
        }
        else {
            savedUser = byEmail.get();
        }

        // Post 저장
        postService.clearAll();
        for (int i = 0; i < 13; i++){
            PostRequest postRequest = PostRequest.builder()
                    .authorNickname("")
                    .title("title" + i)
                    .content("content" + i)
                    .boardCategory(PostCategory.FREE.toString())
                    .isAnonymous(i % 2)
                    .build();
            postRequest.updateAuthor(savedUser.getNickname());
            postService.savePost(savedUser.getId(), postRequest);
        }
        PostRequest postRequest = PostRequest.builder()
                .authorNickname("")
                .title("title13")
                .content("content13")
                .boardCategory(PostCategory.FREE.toString())
                .isAnonymous(1)
                .build();
        postRequest.updateAuthor(mine.getNickname());
        Long savedPostId = postService.savePost(mine.getId(), postRequest);

        // Like 설정
        likeService.likePost(mine.getId(), savedPostId);
        likeService.likePost(savedUser.getId(), savedPostId);

        return ResponseEntity.ok().body
                (new CommonResponse(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage()));
    }
}
