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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
public class PostRepositoryTest {

    @Autowired private PostRepository repository;

    @Test
    @DisplayName("Post 객체 저장 테스트")
    public void repositoryTest(){
        //given
        Post post = getPost("title1", "content1", PostCategory.FREE, 0);

        //when
        Post save = repository.save(post);
        log.info(post.toString());

        //then
        Assertions.assertThat(post).isEqualTo(repository.findById(save.getId()).get());
    }

    @Test
    @DisplayName("자유게시판 Post 객체 페이징 테스트")
    public void pagingPost(){
        //given
        Post post1 = getPost("title1", "content1", PostCategory.FREE, 0);

        Post post2 = getPost("title2", "content2", PostCategory.FREE, 1);
        Post post3 = getPost("title3", "content3", PostCategory.FREE, 0);
        Post post4 = getPost("title4", "content4", PostCategory.GRADE1, 0);
        repository.save(post1);
        repository.save(post2);
        repository.save(post3);
        repository.save(post4);

        //when
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Post> isDeletedFalse = repository.findAllByIsDeletedAndCategory(0, PostCategory.FREE ,pageRequest);
        List<Post> content = isDeletedFalse.getContent();

        //then
        Assertions.assertThat(content.size()).isEqualTo(2);

    }




    private Post getPost(String title, String content, PostCategory category, int isDeleted){
        return Post.builder()
                .title(title)
                .content(content)
                .category(category)
                .isDeleted(isDeleted)
                .build();
    }
}
