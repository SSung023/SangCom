package Project.SangCom.post.repository;


import Project.SangCom.post.domain.Post;
import Project.SangCom.post.domain.PostCategory;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
public class PostRepositoryTest {

    @Autowired private PostRepository repository;

    @Test
    @DisplayName("Post 객체 저장 테스트")
    public void repositoryTest(){
        //given
        Post post = Post.builder()
                .content("content 입니다")
                .title("title 입니다")
                .category(PostCategory.FREE)
                .build();

        //when
        Post save = repository.save(post);
        log.info(post.toString());

        //then
        Assertions.assertThat(post).isEqualTo(repository.findById(save.getId()).get());
    }
}
