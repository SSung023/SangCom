package Project.SangCom.scrap.repository;

import Project.SangCom.post.domain.Post;
import Project.SangCom.post.repository.PostRepository;
import Project.SangCom.scrap.domain.Scrap;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.repository.UserRepository;
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

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
class ScrapRepositoryTest {
    @Autowired private UserRepository userRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private ScrapRepository scrapRepository;



    @Test
    @DisplayName("작성된 게시글에 대해 scrap을 할 수 있다.")
    public void canScrap(){
        //given
        Scrap scrap = new Scrap();
        
        //when
        Scrap savedScrap = scrapRepository.save(scrap);
    }

    @Test
    @DisplayName("사용자와 게시글의 id를 통해 저장한 스크랩을 조회하면 null이 아니어야 한다.")
    public void canFindScrapByUserAndPost(){
        //given
        User user = getUser();
        Post post = getPost("author1");

        Scrap scrap = getScrap(user, post);

        //when
        User savedUser = userRepository.save(user);
        Post savedPost = postRepository.save(post);
        scrapRepository.save(scrap);

        Optional<Scrap> savedScrap = scrapRepository.findSavedScrap(savedUser.getId(), savedPost.getId());

        //then
        Assertions.assertThat(savedScrap).isPresent();
    }

    @Test
    @DisplayName("저장되지 않은 스크랩을 조회하면 Optional.empty()가 반환되어야 한다.")
    public void shouldReturn_optionalEmpty(){
        //given
        User user = getUser();
        Post post = getPost("author1");

        //when
        User savedUser = userRepository.save(user);
        Post savedPost = postRepository.save(post);

        Optional<Scrap> savedScrap = scrapRepository.findSavedScrap(savedUser.getId(), savedPost.getId());

        //then
        Assertions.assertThat(savedScrap).isEmpty();
    }
    
    @Test
    @DisplayName("스크랩했던 글들을 모두 반환받을 수 있다.")
    public void canGetAllScrap(){
        //given
        User user = getUser();
        Post post1 = getPost("name1");
        Post post2 = getPost("name2");

        Scrap scrap1 = getScrap(user, post1);
        Scrap scrap2 = getScrap(user, post2);
        
        //when
        User savedUserId = userRepository.save(user);
        postRepository.save(post1);
        postRepository.save(post2);
        scrapRepository.save(scrap1);
        scrapRepository.save(scrap2);

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));
        Slice<Post> myScraps = scrapRepository.findMyScraps(savedUserId.getId(), pageRequest);

        //then
        Assertions.assertThat(myScraps.getContent().size()).isEqualTo(2);
    }








    private Scrap getScrap(User user, Post post) {
        Scrap scrap = new Scrap();
        scrap.setUser(user);
        scrap.setPost(post);
        return scrap;
    }
    private Post getPost(String author) {
        return Post.builder()
                .title("title")
                .content("content")
                .author(author)
                .build();
    }
    private User getUser(){
        return User.builder()
                .email("test@naver.com")
                .role(Role.STUDENT)
                .build();
    }
}