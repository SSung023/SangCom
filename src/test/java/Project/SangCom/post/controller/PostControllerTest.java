package Project.SangCom.post.controller;


import Project.SangCom.post.dto.PostRequest;
import Project.SangCom.post.service.PostService;
import Project.SangCom.security.dto.AccessTokenUserRequest;
import Project.SangCom.security.service.JwtTokenProvider;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.util.exception.SuccessCode;
import Project.SangCom.utils.WithMockCustomUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    String AUTHORIZATION_HEADER = "Authorization";


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
        String accessToken = getAccessToken();
        
        //when&then
        mockMvc.perform(get("/api/board/free")
                        .header(AUTHORIZATION_HEADER, accessToken))
                .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("접근이 가능하지 않은 ROLE의 경우 자유게시판 api에 접근할 수 없다.")
    @WithMockCustomUser(role = Role.NOT_VERIFIED)
    public void cannotAccessFreeBoardRole() throws Exception {
        //given
        String accessToken = getAccessToken();
        
        //when&then
        mockMvc.perform(get("/api/board/free")
                .header(AUTHORIZATION_HEADER, accessToken))
                .andExpect(status().is4xxClientError());
       
    }

    @Test
    @DisplayName("자유게시판 카테고리에 게시글을 작성(저장)할 수 있다.")
    @WithMockCustomUser(role = Role.STUDENT)
    public void registerFreePost() throws Exception {
        //given
        String accessToken = getAccessToken();
        String requestJson = "{\"id\":\"\", \"boardCategory\":\"FREE\"," +
                "\"author\":\"nickname\", \"title\":\"title\", \"content\":\"content\"," +
                "\"isAnonymous\":\"0\"}";

        //when&then
        mockMvc.perform(post("/api/board/free")
                        .header(AUTHORIZATION_HEADER, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.CREATED.getKey()))
                .andExpect(jsonPath("$.message").value(SuccessCode.CREATED.getMessage()))
                .andExpect(jsonPath("$.data.boardCategory").value("FREE"))
                .andExpect(jsonPath("$.data.author").value("nickname"))
                .andExpect(jsonPath("$.data.title").value("title"))
                .andExpect(jsonPath("$.data.content").value("content"))
                .andExpect(jsonPath("$.data.isAnonymous").value("0"));
    }
    
    @Test
    @DisplayName("자유게시판 카테고리에서 특정 글을 상세 조회할 수 있다.")
    @WithMockCustomUser(role = Role.STUDENT)
    public void viewPostDetails() throws Exception {
        //given
        PostRequest postRequest = getPostRequest("content");

        String accessToken = getAccessToken();
        
        // when
        Long savePostId = postService.savePost(postRequest);

        // then
        mockMvc.perform(get("/api/board/free/" + savePostId)
                        .header(AUTHORIZATION_HEADER, accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.SUCCESS.getKey()))
                .andExpect(jsonPath("$.message").value(SuccessCode.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.boardCategory").value("FREE"))
                .andExpect(jsonPath("$.data.author").value("nickname"))
                .andExpect(jsonPath("$.data.title").value("title"))
                .andExpect(jsonPath("$.data.content").value("content"))
                .andExpect(jsonPath("$.data.isAnonymous").value("0"));
    }

    @Test
    @DisplayName("자유게시판에서 특정 글의 content(내용)을 수정할 수 있다.")
    @WithMockCustomUser(role = Role.STUDENT)
    public void updatePostContent() throws Exception {
        //given
        String accessToken = getAccessToken();
        PostRequest postRequest = getPostRequest("content");
        String requestJson = "{\"id\":\"\", \"boardCategory\":\"FREE\"," +
                "\"author\":\"nickname\", \"title\":\"title\", \"content\":\"new-content\"," +
                "\"isAnonymous\":\"0\"}";

        //when
        Long savePostId = postService.savePost(postRequest);

        //then
        mockMvc.perform(patch("/api/board/free/" + savePostId)
                        .header(AUTHORIZATION_HEADER, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.SUCCESS.getKey()))
                .andExpect(jsonPath("$.message").value(SuccessCode.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.boardCategory").value("FREE"))
                .andExpect(jsonPath("$.data.author").value("nickname"))
                .andExpect(jsonPath("$.data.title").value("title"))
                .andExpect(jsonPath("$.data.content").value("new-content"))
                .andExpect(jsonPath("$.data.isAnonymous").value("0"));

    }




    private User getUser() {
        return User.builder()
                .username("username")
                .nickname("nickname")
                .email("test@naver.com")
                .role(Role.STUDENT)
                .build();
    }
    private String getAccessToken() {
        User user = getUser();
        AccessTokenUserRequest tokenUserRequest = AccessTokenUserRequest.builder()
                .email(user.getEmail())
                .role(user.getRole().getKey())
                .build();

        return provider.createAccessToken(tokenUserRequest);
    }
    private PostRequest getPostRequest(String content) {
        return PostRequest.builder()
                .boardCategory("FREE")
                .author("nickname")
                .title("title")
                .content(content)
                .isAnonymous(0)
                .build();
    }
}