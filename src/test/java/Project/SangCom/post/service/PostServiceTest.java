package Project.SangCom.post.service;

import Project.SangCom.post.domain.Post;
import Project.SangCom.post.dto.PostRequest;
import Project.SangCom.post.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
public class PostServiceTest {

    @Autowired
    private PostRepository repository;
    @Autowired
    private PostService service;

    @Test
    @DisplayName("PostRequest 객체를 Post로 변환할 수 있다.")
    public void convertToPost(){
        //given
        PostRequest postRequest = PostRequest.builder()
                .author("단두대")
                .title("postRequest title")
                .content("postRequest content")
                .boardCategory("FREE")
                .isAnonymous(1) // true
                .build();

        //when
        Post receivedPost = postRequest.toEntity();
        Post savedPost = repository.save(receivedPost);

        //then
        Assertions.assertThat(receivedPost).isEqualTo(savedPost);
    }

    @Test
    @DisplayName("Post 객체로 변환 후, service를 통해 게시글을 DB에 저장할 수 있다.")
    public void canSavePost(){
        //given
        PostRequest request = PostRequest.builder()
                .author("익명1")
                .isAnonymous(1) // true
                .title("title")
                .content("content")
                .boardCategory("FREE")
                .build();

        //when
        Post post = request.toEntity();
        Long registeredId = service.savePost(request);
        log.info(repository.findById(registeredId).get().toString());

        //then

    }
}
