package Project.SangCom.scrap.controller;

import Project.SangCom.scrap.service.ScrapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ScrapController {
    private final ScrapService scrapService;


    /**
     * 사용자가 스크랩한 글들을 전달
     */
//    @GetMapping ("/scrap")
//    public ResponseEntity<PagingResponse<PostResponse>> getScrapedPost
//            (@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
//
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        Slice<PostResponse> scrapedPost = scrapService.findAllScrapedPost(user.getId(), pageable);
//
//        return ResponseEntity.ok().body
//                (new PagingResponse<>(SuccessCode.SUCCESS.getStatus(), SuccessCode.SUCCESS.getMessage(),scrapedPost));
//    }
}
