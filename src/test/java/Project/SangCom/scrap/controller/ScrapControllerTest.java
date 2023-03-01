package Project.SangCom.scrap.controller;

import Project.SangCom.post.domain.PostCategory;
import Project.SangCom.post.dto.PostRequest;
import Project.SangCom.post.service.PostService;
import Project.SangCom.scrap.service.ScrapService;
import Project.SangCom.security.dto.AccessTokenUserRequest;
import Project.SangCom.security.service.JwtTokenProvider;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.repository.UserRepository;
import Project.SangCom.util.exception.ErrorCode;
import Project.SangCom.utils.WithMockCustomUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@Transactional
@Slf4j
@ActiveProfiles({"jwt"})
public class ScrapControllerTest {

    @Value("${jwt.secret}")
    String secretKey;
    @Value("${jwt.refresh-secret}")
    String refreshSecretKey;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    Long refreshTokenValidityInMilliseconds;
    MockMvc mockMvc;
    @Autowired WebApplicationContext context;
    @Autowired JwtTokenProvider provider;
    @Autowired UserRepository userRepository;
    @Autowired PostService postService;
    @Autowired ScrapService scrapService;

    String AUTHORIZATION_HEADER = "Authorization";

    @BeforeEach
    public void setup(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
    


    @Test
    @DisplayName("사용자는 게시글을 스크랩할 수 있다.")
    @WithMockCustomUser
    public void canScrap() throws Exception {
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user2 = getUser("test1@naver.com", "nickname1");
        String accessToken = getAccessToken(user.getEmail(), user.getNickname());

        PostRequest postRequest1 = getPostRequest("title1", "content1", 0);

        //when
        User savedUser2 = userRepository.save(user2);
        Long postId1 = postService.savePost(savedUser2.getId(), PostCategory.FREE, postRequest1);

        //then
        mockMvc.perform(post("/api/scrap/" + postId1)
                .header(AUTHORIZATION_HEADER, accessToken));
    }

    @Test
    @DisplayName("이미 스크랩한 글을 다시 스크랩할 수 없다.")
    @WithMockCustomUser
    public void cannotScrap() throws Exception {
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user2 = getUser("test1@naver.com", "nickname1");
        String accessToken = getAccessToken(user.getEmail(), user.getNickname());

        PostRequest postRequest1 = getPostRequest("title1", "content1", 0);

        //when
        User savedUser2 = userRepository.save(user2);
        Long postId1 = postService.savePost(savedUser2.getId(), PostCategory.FREE, postRequest1);
        scrapService.saveScrap(user.getId(), postId1);

        //then
        mockMvc.perform(post("/api/scrap/" + postId1)
                .header(AUTHORIZATION_HEADER, accessToken))
                .andExpect(jsonPath("$.status").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value(ErrorCode.ALREADY_SCRAPED.getMessage()));
    }

    @Test
    @DisplayName("이미 스크랩한 글에 대해 스크랩 취소할 수 있다.")
    @WithMockCustomUser
    public void unscrap() throws Exception {
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user2 = getUser("test1@naver.com", "nickname1");
        String accessToken = getAccessToken(user.getEmail(), user.getNickname());

        PostRequest postRequest1 = getPostRequest("title1", "content1", 0);

        //when
        User savedUser2 = userRepository.save(user2);
        Long postId1 = postService.savePost(savedUser2.getId(), PostCategory.FREE, postRequest1);
        scrapService.saveScrap(user.getId(), postId1);

        //then
        mockMvc.perform(delete("/api/scrap/" + postId1)
                        .header(AUTHORIZATION_HEADER, accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("사용자가 스크랩한 글들을 받아올 수 있다.")
    @WithMockCustomUser
    public void canScrapedPosts() throws Exception {
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user2 = getUser("test1@naver.com", "nickname1");
        String accessToken = getAccessToken(user.getEmail(), user.getNickname());

        PostRequest postRequest1 = getPostRequest("title1", "content1", 0);
        PostRequest postRequest2 = getPostRequest("title2", "content2", 0);

        //when
        User savedUser2 = userRepository.save(user2);
        Long postId1 = postService.savePost(savedUser2.getId(), PostCategory.FREE, postRequest1);
        Long postId2 = postService.savePost(savedUser2.getId(), PostCategory.FREE, postRequest2);

        scrapService.saveScrap(user.getId(), postId1);
        scrapService.saveScrap(user.getId(), postId2);


        //then
        mockMvc.perform(get("/api/scrap")
                .header(AUTHORIZATION_HEADER, accessToken))
                .andExpect(jsonPath("$.data.numberOfElements").value(2));
    }

    
    
    
    








    private User getUser(String email, String nickname){
        return User.builder()
                .role(Role.STUDENT.getKey())
                .email(email)
                .nickname(nickname)
                .username("username")
                .build();
    }
    private String getAccessToken(String email, String nickname) {
        User user = getUser(email, nickname);
        AccessTokenUserRequest tokenUserRequest = AccessTokenUserRequest.builder()
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        return provider.createAccessToken(tokenUserRequest);
    }
    private PostRequest getPostRequest(String title, String content, int isAnonymous){
        return PostRequest.builder()
                .title(title)
                .content(content)
                .isAnonymous(isAnonymous)
                .build();
    }
}
