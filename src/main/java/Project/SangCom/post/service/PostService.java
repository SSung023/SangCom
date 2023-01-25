package Project.SangCom.post.service;

import Project.SangCom.post.domain.Post;
import Project.SangCom.post.dto.PostRequest;
import Project.SangCom.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
