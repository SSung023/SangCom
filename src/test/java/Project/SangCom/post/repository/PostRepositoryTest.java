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
        Slice<Post> isDeletedFalse = repository.findPostNotDeleted(0, PostCategory.FREE ,pageRequest);
        List<Post> content = isDeletedFalse.getContent();

        //then
        Assertions.assertThat(content.size()).isEqualTo(2);

    }
    
    @Test
    @DisplayName("자유게시판 제목으로 게시글 검색 페이징 테스트")
    public void searchPostByTitle(){
        //given
        Post post1 = getPost("title1", "content1", PostCategory.FREE, 0);
        Post post2 = getPost("title2", "2", PostCategory.FREE, 1);
        Post post3 = getPost("title3", "content3", PostCategory.FREE, 0);
        Post post4 = getPost("title4", "content4", PostCategory.GRADE1, 0);
        repository.save(post1);
        repository.save(post2);
        repository.save(post3);
        repository.save(post4);
        
        //when
        String keyword = "title";
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Post> posts = repository.searchPostByTitle(keyword, PostCategory.FREE, pageRequest);
        List<Post> content = posts.getContent();

        //then
        Assertions.assertThat(content.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("자유게시판 내용으로 게시글 검색 페이징 테스트")
    public void searchPostByContent(){
        //given
        Post post1 = getPost("title1", "content1", PostCategory.FREE, 0);
        Post post2 = getPost("title2", "2", PostCategory.FREE, 1);
        Post post3 = getPost("title3", "content3", PostCategory.FREE, 0);
        Post post4 = getPost("title4", "content4", PostCategory.GRADE1, 0);
        repository.save(post1);
        repository.save(post2);
        repository.save(post3);
        repository.save(post4);

        //when
        String keyword = "content";
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Post> posts = repository.searchPostByContent(keyword, PostCategory.FREE, pageRequest);
        List<Post> content = posts.getContent();

        //then
        Assertions.assertThat(content.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("자유게시판 제목과 내용으로 게시글 검색 페이징 테스트")
    public void searchPostByTitleAndContent(){
        //given
        Post post1 = getPost("keyword", "content1", PostCategory.FREE, 0);
        Post post2 = getPost("title2", "keyword", PostCategory.FREE, 1);
        Post post3 = getPost("title3", "keyword", PostCategory.GRADE2, 0);
        Post post4 = getPost("title4", "content4", PostCategory.GRADE1, 0);
        repository.save(post1);
        repository.save(post2);
        repository.save(post3);
        repository.save(post4);

        //when
        String keyword = "keyword";
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Post> posts = repository.searchPost(keyword, keyword, PostCategory.FREE, pageRequest);
        List<Post> content = posts.getContent();

        //then
        Assertions.assertThat(content.size()).isEqualTo(1);
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
