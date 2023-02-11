package Project.SangCom.comment.controller;

import Project.SangCom.comment.repository.CommentRepository;
import Project.SangCom.comment.service.CommentService;
import Project.SangCom.post.repository.PostRepository;
import Project.SangCom.post.service.PostService;
import Project.SangCom.security.dto.AccessTokenUserRequest;
import Project.SangCom.security.service.JwtTokenProvider;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.repository.UserRepository;
import Project.SangCom.util.exception.SuccessCode;
import Project.SangCom.utils.WithMockCustomUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static Project.SangCom.post.dto.PostResponse.TRUE;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@Slf4j
@ActiveProfiles({"jwt"})
public class CommentControllerTest {
    @Value("${jwt.secret}")
    String secretKey;
    @Value("${jwt.refresh-secret}")
    String refreshSecretKey;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    Long refreshTokenValidityInMilliseconds;
    MockMvc mockMvc; // 스프링 MVC 동작 재현 (가짜 객체 사용)

    @Autowired
    WebApplicationContext context;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    PostService postService;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    CommentService commentService;
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
    @DisplayName("게시글에 댓글을 작성(저장)할 수 있다.")
    @WithMockCustomUser(role = Role.STUDENT)
    public void registerFreeComment() throws Exception {
        //given
        String accessToken = getAccessToken();
        String requestJson = "{\"id\":\"\", \"authorName\":\"nickname\"," +
                " \"date\":\"date\", \"content\":\"content\"," +
                "\"isAnonymous\":\"0\"}";

        //when&then
        mockMvc.perform(post("/api/board/free/{postId}/comment")
                        .header(AUTHORIZATION_HEADER, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.CREATED.getKey()))
                .andExpect(jsonPath("$.message").value(SuccessCode.CREATED.getMessage()))
                .andExpect(jsonPath("$.data.authorName").value("nickname"))
                .andExpect(jsonPath("$.data.date").value("date"))
                .andExpect(jsonPath("$.data.content").value("content"))
                .andExpect(jsonPath("$.data.isOwner").value(TRUE))
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
}
