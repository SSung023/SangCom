package Project.SangCom.post.service;

import Project.SangCom.post.domain.Post;
import Project.SangCom.post.dto.PostRequest;
import Project.SangCom.post.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

//@SpringBootTest
@Transactional
@Slf4j
public class PostServiceTest {

    @MockBean
    private PostRepository repository;
    @InjectMocks
    private PostService service;

    @Test
    @DisplayName("PostRequest 객체를 Post로 변환할 수 있다.")
    public void convertToPost(){
        //given
        PostRequest postRequest = PostRequest.builder()
                .author("단두대")
                .title("postRequest title")
                .content("postRequest content")
                .isAnonymous(1)
                .build();

        //when
        Post receivedPost = postRequest.toEntity();

        //then
        log.info(receivedPost.toString());

    }
}
