package Project.SangCom.scrap.service;

import Project.SangCom.post.domain.Post;
import Project.SangCom.post.dto.PostResponse;
import Project.SangCom.post.service.PostService;
import Project.SangCom.scrap.domain.Scrap;
import Project.SangCom.scrap.repository.ScrapRepository;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.service.UserService;
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ScrapService {
    private final UserService userService;
    private final PostService postService;
    private final ScrapRepository scrapRepository;


    /**
     * 사용자(user)와 게시글(post)의 id(PK)를 통해 스크랩을 저장
     */
    @Transactional
    public Long saveScrap(Long userId, Long postId) {
        if (scrapRepository.findSavedScrap(userId, postId).isPresent()){
            throw new BusinessException(ErrorCode.ALREADY_SCRAPED);
        }

        User user = userService.findUserById(userId);
        Post post = postService.findPostById(postId);

        if (postService.checkIsPostOwner(user, postId) == 1){
            // 게시글 작성자라면 예외 발생
            throw new BusinessException(ErrorCode.OWNER_NOT_ALLOWED);
        }

        Scrap scrap = new Scrap();
        scrap.setUser(user);
        scrap.setPost(post);

        Scrap savedScrap = scrapRepository.save(scrap);
        return savedScrap.getId();
    }

    /**
     * scrap의 id(PK)를 통해 DB에서 스크랩 객체를 찾음
     * @param scrapId 찾고자 하는 scrap의 Id(PK)
     */
    public Scrap findScrapById(Long scrapId) {
        return scrapRepository.findById(scrapId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));
    }

    /**
     * 사용자가 스크랩한 게시글에 대해 스크랩을 다시 누르면 스크랩 취소 처리
     */
    @Transactional
    public void unscrap(Long userId, Long postId) {
        Scrap scrap = scrapRepository.findSavedScrap(userId, postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));

        scrapRepository.delete(scrap);
    }

    /**
     * 사용자가 스크랩했던 글들을 조회하여 PostResponse로 변환하여 반환
     * PostResponse에 대해서 좋아요/스크랩한 글인지 확인하는 과정이 필요
     * @param user 작성한 글을 조회할 대상의 사용자
     */
    public Slice<PostResponse> findAllScrapedPost(User user, Pageable pageable){
        List<PostResponse> posts = scrapRepository.findMyScraps(user.getId(), pageable).stream()
                .map(s -> s.getPost())
                .collect(Collectors.toList())
                .stream().map(p -> postService.convertToMyPageResponse(user, p))
                .collect(Collectors.toList());

        return new PageImpl<>(posts);
    }
}
