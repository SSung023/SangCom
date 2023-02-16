package Project.SangCom.scrap.controller;

import Project.SangCom.post.dto.PostResponse;
import Project.SangCom.scrap.service.ScrapService;
import Project.SangCom.user.domain.User;
import Project.SangCom.util.exception.SuccessCode;
import Project.SangCom.util.response.dto.CommonResponse;
import Project.SangCom.util.response.dto.PagingResponse;
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
@RequestMapping("/api")
@Slf4j
public class ScrapController {
    private final ScrapService scrapService;


    /**
     * 사용자가 특정 게시글에 대해 스크랩 진행
     * @param postId 스크랩할 게시글의 id(PK)
     */
    @PostMapping("/scrap/{postId}")
    public ResponseEntity<CommonResponse> scrapPost(@PathVariable Long postId){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        scrapService.saveScrap(user.getId(), postId);

        return ResponseEntity.ok().body
                (new CommonResponse(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage()));
    }

    /**
     * 사용자가 스크랩한 게시글에 대해 스크랩 취소 진행
     * @param postId 스크랩 취소할 게시글의 id(PK)
     */
    @DeleteMapping("/scrap/{postId}")
    public ResponseEntity<CommonResponse> unscrapPost(@PathVariable Long postId){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        scrapService.unscrap(user.getId(), postId);

        return ResponseEntity.ok().body
                (new CommonResponse(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage()));
    }

    @GetMapping ("/scrap")
    public ResponseEntity<PagingResponse<PostResponse>> getScrapedPost
            (@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Slice<PostResponse> scrapedPost = scrapService.findAllScrapedPost(user, pageable);

        return ResponseEntity.ok().body
                (new PagingResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(),scrapedPost));
    }
}
