package Project.SangCom.scrap.controller;

import Project.SangCom.post.dto.PostResponse;
import Project.SangCom.scrap.dto.ScrapRequest;
import Project.SangCom.scrap.service.ScrapService;
import Project.SangCom.user.domain.User;
import Project.SangCom.util.exception.SuccessCode;
import Project.SangCom.util.response.dto.ListResponse;
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
