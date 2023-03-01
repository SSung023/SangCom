package Project.SangCom.scrap.service;

import Project.SangCom.post.domain.PostCategory;
import Project.SangCom.post.dto.PostRequest;
import Project.SangCom.post.dto.PostResponse;
import Project.SangCom.post.service.PostService;
import Project.SangCom.scrap.domain.Scrap;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.service.UserService;
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
class ScrapServiceTest {
    @Autowired private UserService userService;
    @Autowired private PostService postService;
    @Autowired private ScrapService scrapService;


    @Test
    @DisplayName("사용자는 등록되어 있는 게시글을 스크랩할 수 있다.")
    public void canSaveScrap(){
        //given
        User user1 = getUser("test1@naver.com", "nickname1");
        User user2 = getUser("test2@naver.com", "nickname2");
        PostRequest postRequest = getPostRequest();

        //when
        Long savedUserId1 = userService.saveUser(user1);
        Long savedUserId2 = userService.saveUser(user2);

        Long savedPostId = postService.savePost(savedUserId1, PostCategory.FREE, postRequest);
        Long savedScrapId = scrapService.saveScrap(savedUserId2, savedPostId);
    }

    @Test
    @DisplayName("등록되어 있는 스크랩을 id(PK)를 통해 찾을 수 있다.")
    public void canFindScrapById(){
        //given
        User user1 = getUser("test1@naver.com", "nickname1");
        User user2 = getUser("test2@naver.com", "nickname2");
        PostRequest postRequest = getPostRequest();

        //when
        Long savedUserId1 = userService.saveUser(user1);
        Long savedUserId2 = userService.saveUser(user2);

        Long savedPostId = postService.savePost(savedUserId1, PostCategory.FREE, postRequest);
        Long savedScrapId = scrapService.saveScrap(savedUserId2, savedPostId);

        Scrap scrap = scrapService.findScrapById(savedScrapId);

        //then
        Assertions.assertThat(scrap.getId()).isEqualTo(savedScrapId);
    }

    @Test
    @DisplayName("스크랩이 이미 처리된 게시글에 대해서는 재 스크랩이 허용되지 않는다.")
    public void cannotScrap_thatAlreadyScraped(){
        //given
        User user1 = getUser("test1@naver.com", "nickname1");
        User user2 = getUser("test2@naver.com", "nickname2");
        PostRequest postRequest = getPostRequest();

        //when
        Long savedUserId1 = userService.saveUser(user1);
        Long savedUserId2 = userService.saveUser(user2);

        Long savedPostId = postService.savePost(savedUserId1, PostCategory.FREE, postRequest);
        Long savedScrapId = scrapService.saveScrap(savedUserId2, savedPostId);

        //then
        Assertions.assertThatThrownBy(() -> scrapService.saveScrap(savedUserId2, savedPostId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.ALREADY_SCRAPED.getMessage());
    }

    @Test
    @DisplayName("작성자는 자신이 작성한 게시글에 대해 스크랩이 허용되지 않는다.")
    public void ownerCannnotScrap(){
        //given
        User user1 = getUser("test1@naver.com", "nickname1");
        PostRequest postRequest = getPostRequest();

        //when
        Long savedUserId1 = userService.saveUser(user1);
        Long savedPostId = postService.savePost(savedUserId1, PostCategory.FREE, postRequest);

        //then
        Assertions.assertThatThrownBy(() -> scrapService.saveScrap(savedUserId1, savedPostId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.OWNER_NOT_ALLOWED.getMessage());
    }

    @Test
    @DisplayName("사용자는 스크랩한 글에 대해 스크랩 취소한 이후 스크랩을 찾으면 오류가 발생한다.")
    public void canUndoScrap(){
        //given
        User user1 = getUser("test1@naver.com", "nickname1");
        User user2 = getUser("test2@naver.com", "nickname2");
        PostRequest postRequest = getPostRequest();

        //when
        Long savedUserId1 = userService.saveUser(user1);
        Long savedUserId2 = userService.saveUser(user2);

        Long savedPostId = postService.savePost(savedUserId1, PostCategory.FREE, postRequest);
        Long savedScrapId = scrapService.saveScrap(savedUserId2, savedPostId);

        scrapService.unscrap(savedUserId2, savedPostId);

        //then
        Assertions.assertThatThrownBy(() ->scrapService.findScrapById(savedScrapId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.DATA_ERROR_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("스크랩했던 모든 게시글들을 PostResponse로 변환하여 받아올 수 있다.")
    public void canGetAllScrapedPost(){
        //given
        User user1 = getUser("test1@naver.com", "nickname1");
        User user2 = getUser("test2@naver.com", "nickname2");
        PostRequest postRequest1 = getPostRequest();
        PostRequest postRequest2 = getPostRequest();

        //when
        Long savedUserId1 = userService.saveUser(user1);
        Long savedUserId2 = userService.saveUser(user2);
        Long savedPostId1 = postService.savePost(savedUserId1, PostCategory.FREE, postRequest1);
        Long savedPostId2 = postService.savePost(savedUserId1, PostCategory.FREE, postRequest2);

        scrapService.saveScrap(savedUserId2, savedPostId1);
        scrapService.saveScrap(savedUserId2, savedPostId2);

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));
        Slice<PostResponse> scrapPosts = scrapService.findAllScrapedPost(userService.findUserById(savedUserId2), pageRequest);

        //then
        Assertions.assertThat(scrapPosts.getContent().size()).isEqualTo(2);
        Assertions.assertThat(scrapPosts.getContent().get(0)).isInstanceOf(PostResponse.class);
    }








    private User getUser(String email, String nickname){
        return User.builder()
                .role(Role.STUDENT.getKey())
                .email(email)
                .nickname(nickname)
                .username("username")
                .build();
    }
    private PostRequest getPostRequest() {
        return PostRequest.builder()
                .authorNickname("")
                .title("title")
                .content("content")
                .isAnonymous(0)
                .build();
    }
}