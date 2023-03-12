package Project.SangCom.post.controller;

import Project.SangCom.like.dto.LikeDTO;
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
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ErrorCode;
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
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class PostController {
    private final PostService postService;
    private final LikeService likeService;
    private final int PAGE_SIZE = 20;

    //for test
    private final UserService userService;
    private final UserRepository userRepository;


    /**
     * 실시간 인기글 조회 -> 자유게시판, 학년별게시판, (동아리게시판)
     * ex) /api/board/free/best
     */
    @GetMapping("/board/{category}/best")
    public ResponseEntity<SingleResponse<PostResponse>> getFreeBoardInfo
        (@PageableDefault(size = PAGE_SIZE, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
        , @PathVariable String category){

        /**
        if(category.equals("SUGGESTION") && category.equals("COUNCIL"))
            return 예외처리; -> 그냥 api 사용x..?
        */

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PostCategory postCategory = postService.checkCategory(category);

        List<Post> mostLikedPost = postService.getMostLikedPost(postCategory, pageable);
        PostResponse postResponse = null;
        if (!mostLikedPost.isEmpty()){
            postResponse = postService.convertToPreviewResponse(user, mostLikedPost.get(0));
        }

        return ResponseEntity.ok().body
                (new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), postResponse));
    }

    /**
     * 전체 글 조회
     * 건의게시판 -> 조회는 가능하나 미리보기 시 isSecret에 따라 감추기
     * ex) /board/free/list?page=0
     */
    @GetMapping("/board/{category}/list")
    public ResponseEntity<PagingResponse<PostResponse>> getFreePostList
        (@PageableDefault(size = PAGE_SIZE, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
        , @PathVariable String category){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PostCategory postCategory = postService.checkCategory(category);

        Slice<PostResponse> postList = postService.getNotDeletedPostList(user, postCategory, pageable);

        return ResponseEntity.ok().body
                (new PagingResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(),postList));
    }

    /**
     * 자유게시판 게시글 검색 - 검색하는 방법(query): 제목(title)/내용(content)/제목+내용(all)
     * ex) /api/board/free/search?query=title&keyword=something&page=0
     */
    @GetMapping("/board/{category}/search")
    public ResponseEntity<PagingResponse<PostResponse>> searchPosts
        (@PageableDefault(size = PAGE_SIZE, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
         @PathVariable String category, @RequestParam String query, @RequestParam String keyword){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PostCategory postCategory = postService.checkCategory(category);

        Slice<PostResponse> postList = postService.searchPosts(user, query, keyword, postCategory, pageable);
        return ResponseEntity.ok().body
                (new PagingResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(),postList));
    }

    /**
     * 자유게시판 특정 글 조회
     * 응답 객체에 isOwner(게시글 작성자 여부)에 대한 값 필요
     * 응답 객체에 isLikePressed(좋아요 선택 여부)에 대한 값 필요
     *
     * ex) /api/board/free/23
     */
    @GetMapping("/board/{category}/{postId}")
    public ResponseEntity<SingleResponse<PostResponse>> inquiryCertainFreePost
            (@PathVariable Long postId, @PathVariable String category){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Post postById = postService.findPostById(postId);
        PostResponse postDetailResponse = postService.convertToDetailResponse(user, postById);

        /* 건의게시판 비밀 글 접근 제한 */
        if (category.equals("suggestion")) {
            if (user.getRole().equals("student_council") || postService.checkIsPostOwner(user, postId) == 1)
                return ResponseEntity.ok().body
                        (new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), postDetailResponse));
            else
                throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        return ResponseEntity.ok().body
                (new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), postDetailResponse));
    }

    /**
     * 자유게시판 특정 글 작성
     * 응답 객체에 isOwner(게시글 작성자 여부)에 대한 값 필요
     *
     * ex) /api/board/free
     */
    @PostMapping("/board/{category}")
    public ResponseEntity<SingleResponse<PostResponse>> registerPost
            (@RequestBody PostRequest postRequest, @PathVariable String category){

        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        postRequest.updateAuthor(writer.getNickname());

        PostCategory postCategory = postService.checkCategory(category);
        Long savedPostId = postService.savePost(writer.getId(), postCategory, postRequest);

        PostResponse postDetailResponse = postService.convertToDetailResponse(writer, savedPostId);

        return ResponseEntity.ok().body
                (new SingleResponse<>(SuccessCode.CREATED.getStatus(),SuccessCode.CREATED.getMessage(), postDetailResponse));
    }

    /**
     * 자유게시판 특정 글 수정
     * 응답 객체에 isOwner(게시글 작성자 여부)에 대한 값 필요
     *
     * ex) /api/board/free/23
     */
    @PatchMapping("/board/{category}/{postId}")
    public ResponseEntity<SingleResponse<PostResponse>> modifyPost
            (@PathVariable Long postId, @PathVariable String category, @RequestBody PostRequest postRequest){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PostCategory postCategory = postService.checkCategory(category);

        Long updatedPostId = postService.updatePost(postId, postRequest);
        PostResponse postDetailResponse = postService.convertToDetailResponse(user, updatedPostId);

        return ResponseEntity.ok().body
                (new SingleResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), postDetailResponse));
    }

    /**
     * 자유게시판 특정 글 삭제
     * ex) /api/board/2
     */
    @DeleteMapping("/board/{postId}")
    public ResponseEntity<CommonResponse> deletePost(@PathVariable Long postId){
        postService.deletePost(postId);

        return ResponseEntity.ok().body
                (new CommonResponse(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage()));
    }

    /**
     * 건의게시판 특정 글 해결 여부
     * ex) /api/board/suggestion/4
     * @return

    @PatchMapping("/board/suggestion/{postId}/solve")
    public ResponseEntity<CommonResponse> solvePost(@PathVariable Long postId, @PathVariable String category){
        postService.solvePost(postId);

        return ResponseEntity.ok().body
                (new CommonResponse(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage()));
    }*/

    @GetMapping("/board/preview/{category}")
    public ResponseEntity<ListResponse<PostResponse>> getRecentPosts
                (@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                 @PathVariable String category){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PostCategory postCategory = postService.checkCategory(category);
        List<PostResponse> previewPosts = postService.getPreviewPosts(user, postCategory, pageable);

        return ResponseEntity.ok().body
                (new ListResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(), previewPosts));
    }

//    @PostMapping("/board/preview")
//    public ResponseEntity<CommonResponse> changePreviewPin(){
//
//    }





    //=== 테스트용 컨트롤러 ===//
    @GetMapping("/board/test")
    public ResponseEntity<CommonResponse> testPost(){
        // User 객체 생성
        User mine = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<User> byEmail = userRepository.findByEmail("test@naver.com");
        User savedUser;
        if (byEmail.isEmpty()){
            User user = User.builder()
                    .role(Role.STUDENT.getKey())
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

        savePost(savedUser, PostCategory.FREE, "내일 학교 가야되네", "어째서......", 1);
        savePost(savedUser, PostCategory.FREE, "우와 이게 뭐야", "신기하다", 1);
        savePost(savedUser, PostCategory.FREE, "여자 봄 옷", "어디서 사?? 온 오프 상관없이 추천해주라 ㅠㅠㅠㅠㅠ", 1);
        savePost(savedUser, PostCategory.FREE, "넷플", "보고 싶은게 있는데 ㅠㅠㅠ 혼자 결제하긴 좀 그래서.. 남는 자리 있나요..ㅠㅠㅠ", 1);
        savePost(savedUser, PostCategory.FREE, "보드게임카페", "자주 가는 곳 있음 추천해줘!!! 가보고 싶은데 어디가야할지 모르겠어", 1);
        savePost(savedUser, PostCategory.FREE, "친구들아", "사랑한다", 1);
        Long savedPostId = savePost(mine, PostCategory.FREE, "솔직히", "이제 방학할 때 되지 않았어?", 1);
        savePost(savedUser, PostCategory.FREE, "오랜만에", "타자 연습에서 별 헤는 밤 쳐봤는데 추억돋는다 ㅋㅋㅋㅋ", 1);
        savePost(savedUser, PostCategory.FREE, "우리집 고양이", "너무 귀여워", 1);
        savePost(savedUser, PostCategory.FREE, "봄 되니까", "수업 들으면서 너무 졸려..", 1);
        savePost(mine, PostCategory.FREE, "오늘 급식", "진짜 맛있었다 최고야. 짜릿해.", 1);


        savePost(savedUser, PostCategory.GRADE1, "친구들아", "사랑한다", 1);
        savePost(mine, PostCategory.GRADE1, "솔직히", "이제 방학할 때 되지 않았어?", 1);
        savePost(savedUser, PostCategory.GRADE1, "오랜만에", "타자 연습에서 별 헤는 밤 쳐봤는데 추억돋는다 ㅋㅋㅋㅋ", 1);
        savePost(savedUser, PostCategory.GRADE1, "우리집 고양이", "너무 귀여워", 1);
        savePost(savedUser, PostCategory.GRADE1, "봄 되니까", "수업 들으면서 너무 졸려..", 1);
        Long savedPostId2 = savePost(mine, PostCategory.GRADE1, "오늘 급식", "진짜 맛있었다 최고야. 짜릿해.", 1);
        savePost(savedUser, PostCategory.GRADE1, "내일 학교 가야되네", "어째서......", 1);
        savePost(savedUser, PostCategory.GRADE1, "우와 이게 뭐야", "신기하다", 1);
        savePost(savedUser, PostCategory.GRADE1, "여자 봄 옷", "어디서 사?? 온 오프 상관없이 추천해주라 ㅠㅠㅠㅠㅠ", 1);
        savePost(savedUser, PostCategory.GRADE1, "넷플", "보고 싶은게 있는데 ㅠㅠㅠ 혼자 결제하긴 좀 그래서.. 남는 자리 있나요..ㅠㅠㅠ", 1);
        savePost(savedUser, PostCategory.GRADE1, "보드게임카페", "자주 가는 곳 있음 추천해줘!!! 가보고 싶은데 어디가야할지 모르겠어", 1);

        savePost(savedUser, PostCategory.GRADE2, "오랜만에", "타자 연습에서 별 헤는 밤 쳐봤는데 추억돋는다 ㅋㅋㅋㅋ", 1);
        savePost(savedUser, PostCategory.GRADE2, "우리집 고양이", "너무 귀여워", 1);
        savePost(savedUser, PostCategory.GRADE2, "봄 되니까", "수업 들으면서 너무 졸려..", 1);
        Long savedPostId3 = savePost(mine, PostCategory.GRADE2, "오늘 급식", "진짜 맛있었다 최고야. 짜릿해.", 1);
        savePost(savedUser, PostCategory.GRADE2, "내일 학교 가야되네", "어째서......", 1);
        savePost(savedUser, PostCategory.GRADE2, "우와 이게 뭐야", "신기하다", 1);
        savePost(savedUser, PostCategory.GRADE2, "여자 봄 옷", "어디서 사?? 온 오프 상관없이 추천해주라 ㅠㅠㅠㅠㅠ", 1);
        savePost(savedUser, PostCategory.GRADE2, "넷플", "보고 싶은게 있는데 ㅠㅠㅠ 혼자 결제하긴 좀 그래서.. 남는 자리 있나요..ㅠㅠㅠ", 1);
        savePost(savedUser, PostCategory.GRADE2, "보드게임카페", "자주 가는 곳 있음 추천해줘!!! 가보고 싶은데 어디가야할지 모르겠어", 1);
        savePost(savedUser, PostCategory.GRADE2, "친구들아", "사랑한다", 1);
        savePost(mine, PostCategory.GRADE2, "솔직히", "이제 방학할 때 되지 않았어?", 1);

        savePost(savedUser, PostCategory.GRADE3, "내일 학교 가야되네", "어째서......", 1);
        savePost(mine, PostCategory.GRADE3, "솔직히", "이제 방학할 때 되지 않았어?", 1);
        savePost(savedUser, PostCategory.GRADE3, "오랜만에", "타자 연습에서 별 헤는 밤 쳐봤는데 추억돋는다 ㅋㅋㅋㅋ", 1);
        savePost(savedUser, PostCategory.GRADE3, "우리집 고양이", "너무 귀여워", 1);
        savePost(savedUser, PostCategory.GRADE3, "봄 되니까", "수업 들으면서 너무 졸려..", 1);
        Long savedPostId4 = savePost(mine, PostCategory.GRADE3, "오늘 급식", "진짜 맛있었다 최고야. 짜릿해.", 1);
        savePost(savedUser, PostCategory.GRADE3, "우와 이게 뭐야", "신기하다", 1);
        savePost(savedUser, PostCategory.GRADE3, "여자 봄 옷", "어디서 사?? 온 오프 상관없이 추천해주라 ㅠㅠㅠㅠㅠ", 1);
        savePost(savedUser, PostCategory.GRADE3, "넷플", "보고 싶은게 있는데 ㅠㅠㅠ 혼자 결제하긴 좀 그래서.. 남는 자리 있나요..ㅠㅠㅠ", 1);
        savePost(savedUser, PostCategory.GRADE3, "보드게임카페", "자주 가는 곳 있음 추천해줘!!! 가보고 싶은데 어디가야할지 모르겠어", 1);
        savePost(savedUser, PostCategory.GRADE3, "친구들아", "사랑한다", 1);

        savePost(mine, PostCategory.COUNCIL, "동아리 모집 기간", "동아리 모집 기간은 2023년 3월 10일부터 3월 20일까지 입니다!", 0);
        savePost(mine, PostCategory.COUNCIL, "2023년 축제 부스 운영", "이번 2023년도 축제 부스 운영에 관한 글입니다.\n부스는 총 8개 운영할 예정이며, 각 특수 활동 실에서 진행됩니다.", 0);
        savePost(mine, PostCategory.COUNCIL, "학생회장 당선 인사", "안녕하세요, 상명대학교 사범대부속 여자고등학교 2023년도 학생회장에 당선된 홍길동입니다.", 0);
        savePost(mine, PostCategory.COUNCIL, "아침 간식 행사", "개학하고 적응하시느라 많이 힘드셨죠?! 3월 20일 월요일부터 3월 24일 금요일까지 등교 시간에 간단한 간식을 나누어 드립니다!", 0);
        savePost(mine, PostCategory.COUNCIL, "교복 규정 변경", "2023년도 3월부터 교복 규정이 바뀝니다!", 0);

        savePost(mine, PostCategory.SUGGESTION, "우천 시 우산 꽃이", "비 왔을 때 우산 꽃이가 반에 하나 씩 있는데 부족합니다 ㅠㅠ 반 당 하나씩 더 있으면 좋을 것 같습니다..", 0);


        // Like 설정
        likeService.clearAll();
        likeService.toggleLikePost(mine.getId(), savedPostId);
        likeService.toggleLikePost(savedUser.getId(), savedPostId);

        likeService.toggleLikePost(mine.getId(), savedPostId2);
        likeService.toggleLikePost(savedUser.getId(), savedPostId2);

        likeService.toggleLikePost(mine.getId(), savedPostId3);
        likeService.toggleLikePost(savedUser.getId(), savedPostId3);

        likeService.toggleLikePost(mine.getId(), savedPostId4);
        likeService.toggleLikePost(savedUser.getId(), savedPostId4);



        return ResponseEntity.ok().body
                (new CommonResponse(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage()));
    }

    private Long savePost(User user, PostCategory postCategory, String title, String content, int isAnonymous){
        PostRequest postRequest = PostRequest.builder()
                .authorNickname(user.getNickname())
                .isAnonymous(isAnonymous)
                .title(title)
                .content(content)
                .build();
        return postService.savePost(user.getId(), postCategory, postRequest);
    }
}
