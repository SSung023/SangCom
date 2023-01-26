package Project.SangCom.post.service;

import Project.SangCom.post.domain.Post;
import Project.SangCom.post.dto.PostRequest;
import Project.SangCom.post.dto.PostResponse;
import Project.SangCom.post.repository.PostRepository;
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
    private final PostRepository repository;


    /**
     * RequestDTO를 Entity로 변환하고 repository를 통해 저장
     * @param postRequest 사용자에게 전달받은 게시글 정보
     */
    public Long savePost(PostRequest postRequest) {
        Post post = postRequest.toEntity();
        Post savedPost = repository.save(post);

        return savedPost.getId();
    }

    /**
     * @param postId repository(DB)에서 찾고자 하는 Post의 id
     */
    public Optional<Post> findPostById(Long postId) {
        return repository.findById(postId);
    }



    /**
     * 자유게시판에 전달할 PostResponse(Post) 객체로 반환
     * @param postId PostResponse로 변환하고 싶은 post의 PK
     */
    public PostResponse convertToResponse(Long postId){
        Post post = repository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));

        return PostResponse.builder()
                .id(post.getId())
                .boardCategory(post.getCategory().toString())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getAuthor())
                .isAnonymous(post.getIsAnonymous())
                .build();
    }
}
