package Project.SangCom.post.controller;


import Project.SangCom.post.dto.PostRequest;
import Project.SangCom.post.service.PostService;
import Project.SangCom.security.dto.AccessTokenUserRequest;
import Project.SangCom.security.service.JwtTokenProvider;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.repository.UserRepository;
import Project.SangCom.user.service.UserService;
import Project.SangCom.utils.WithMockCustomUser;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@Slf4j
class PostControllerTest {
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext context;
    @Autowired
    PostService postService;
    @Autowired
    JwtTokenProvider provider;


    @BeforeEach
    public void setup(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

    }


    @Test
    @DisplayName("STUDENT ROLE을 가지고 있는 사용자는 자유게시판 api에 접근할 수 있다.")
    @WithMockCustomUser(role = Role.STUDENT)
    public void studentCanAccessFreeBoard() throws Exception {
        //given
        User user = getUser();
        String accessToken = getAccessToken(user);

        //when

        //then
        mockMvc.perform(get("/api/board/free")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("자유게시판 카테고리에 게시글을 작성(저장)할 수 있다.")
    @WithMockCustomUser
    public void registerFreePost() throws Exception {
        //given
        PostRequest postRequest = PostRequest.builder()
                .boardCategory("FREE")
                .author("nickname")
                .title("title")
                .content("content")
                .isAnonymous(0)
                .build();

        User user = getUser();
        String accessToken = getAccessToken(user);
        
        //when
        Long savePostId = postService.savePost(postRequest);

        //then
        mockMvc.perform(post("/api/board/free")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk());
    }



    private User getUser() {
        User user = User.builder()
                .username("username")
                .nickname("nickname")
                .email("test@naver.com")
                .role(Role.STUDENT)
                .build();
        return user;
    }
    private String getAccessToken(User user) {
        AccessTokenUserRequest tokenUserRequest = AccessTokenUserRequest.builder()
                .email(user.getEmail())
                .role(user.getRole().getKey())
                .build();

        String accessToken = provider.createAccessToken(tokenUserRequest);
        return accessToken;
    }
}