package Project.SangCom.comment.controller;

import Project.SangCom.comment.domain.Comment;
import Project.SangCom.comment.dto.CommentRequest;
import Project.SangCom.comment.repository.CommentRepository;
import Project.SangCom.comment.service.CommentService;
import Project.SangCom.post.domain.Post;
import Project.SangCom.post.domain.PostCategory;
import Project.SangCom.post.repository.PostRepository;
import Project.SangCom.post.service.PostService;
import Project.SangCom.security.dto.AccessTokenUserRequest;
import Project.SangCom.security.service.JwtTokenProvider;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.repository.UserRepository;
import Project.SangCom.util.exception.SuccessCode;
import Project.SangCom.utils.WithMockCustomUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.persistence.EntityManager;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Project.SangCom.post.dto.PostResponse.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
//@AutoConfigureMockMvc
//@WebAppConfiguration
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
    CommentRepository commentRepository;
    @Autowired
    PostService postService;
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
    @DisplayName("댓글 api 접근")
    @WithMockCustomUser(role = Role.STUDENT)
    public void studentCanAccessComment() throws Exception {
        //given
        String accessToken = getAccessToken();
        Long postId = getPost().getId();

        //when&then
        // controller에서 경로 comment로 바꾸고
        // get("/api/board/free/comment") 로 하면 테스트 통과

        mockMvc.perform(get("/api/board/free/" + postId +"/comment")
                        .header(AUTHORIZATION_HEADER, accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("댓글 조회")
    @WithMockCustomUser
    public void getComments() throws Exception {
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String accessToken = getAccessToken();
        Post post = getPost();
        CommentRequest request1 = getCommentRequest("comment1");
        CommentRequest request2 = getCommentRequest("comment2");
        CommentRequest request3 = getCommentRequest("comment3");

        commentService.saveComment(user.getId(), post.getId(), request1);
        commentService.saveComment(user.getId(), post.getId(), request2);
        commentService.saveComment(user.getId(), post.getId(), request3);

        //when&then
        mockMvc.perform(get("/api/board/free/" + post.getId() + "/comment")
                .header(AUTHORIZATION_HEADER, accessToken))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value(SuccessCode.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.count").value(3));

        // post 객체에 제대로 들어있는지 확인
        List<Comment> comments = post.getComments();
        assertThat(post.getComments().size()).isEqualTo(3);
        assertThat(post.getComments().get(0).getContent()).isEqualTo("comment1");
        assertThat(post.getComments().get(1).getContent()).isEqualTo("comment2");
        assertThat(post.getComments().get(2).getContent()).isEqualTo("comment3");
    }

    @Test
    @DisplayName("댓글 작성")
    @WithMockCustomUser(role = Role.STUDENT)
    public void registerComment() throws Exception {
        //given
        String accessToken = getAccessToken();
        String requestJson = "{\"id\":\"\", \"authorName\":\"\"," +
                "\"content\":\"content\", \"isAnonymous\":\"0\"}";
        Long postId = getPost().getId();

        //when&then
        mockMvc.perform(post("/api/board/free/" + postId + "/comment")
                        .header(AUTHORIZATION_HEADER, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.CREATED.getKey()))
                .andExpect(jsonPath("$.message").value(SuccessCode.CREATED.getMessage()))
                .andExpect(jsonPath("$.data.authorName").value("nickname"))
                .andExpect(jsonPath("$.data.content").value("content"))
                .andExpect(jsonPath("$.data.isOwner").value(TRUE))
                .andExpect(jsonPath("$.data.isAnonymous").value("0"));

        // post 객체 확인
        assertThat(postService.findPostById(postId).getComments().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("대댓글 작성")
    @WithMockCustomUser(role = Role.STUDENT)
    public void registerReComment() throws Exception {
        //given
        String accessToken = getAccessToken();
        String requestJson = "{\"id\":\"\", \"authorName\":\"nickname\"," +
                "\"content\":\"content\", \"isAnonymous\":\"0\"}";

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Post post = getPost();
        CommentRequest request = getCommentRequest("comment");
        Long parentId = commentService.saveComment(user.getId(), post.getId(), request);

        //when&then
        mockMvc.perform(post("/api/board/free/" + post.getId() + "/comment/" + parentId)
                        .header(AUTHORIZATION_HEADER, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SuccessCode.CREATED.getKey()))
                .andExpect(jsonPath("$.message").value(SuccessCode.CREATED.getMessage()))
                .andExpect(jsonPath("$.data.authorName").value("nickname"))
                .andExpect(jsonPath("$.data.content").value("content"))
                .andExpect(jsonPath("$.data.isOwner").value(TRUE))
                .andExpect(jsonPath("$.data.isAnonymous").value("0"));

        assertThat(post.getComments().size()).isEqualTo(1);
        assertThat(post.getComments().get(0).getChildList().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("대댓글이 없는 댓글 삭제")
    @WithMockCustomUser
    public void deleteComment() throws Exception {
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String accessToken = getAccessToken();
        Post post = getPost();
        CommentRequest request1 = getCommentRequest("comment1");
        CommentRequest request2 = getCommentRequest("comment2");
        CommentRequest request3 = getCommentRequest("comment3");

        commentService.saveComment(user.getId(), post.getId(), request1);
        commentService.saveComment(user.getId(), post.getId(), request2);
        Long savedCommentId = commentService.saveComment(user.getId(), post.getId(), request3);


        //when&then
        mockMvc.perform(delete("/api/board/free/"+ post.getId() + "/comment/" + savedCommentId)
                .header(AUTHORIZATION_HEADER, accessToken))
                .andExpect(status().isOk());

        Assertions.assertThat(post.getComments().size()).isEqualTo(3);

    }

    /**
     * 부모 삭제
     * 대댓글 존재
     * DB와 화면에서는 지워지지 않고, "삭제된 댓글입니다"라고 표시
     *
    @Test
    @DisplayName("부모 삭제 - 대댓글 존재")
    public void delComment_1() throws Exception {
        //given
        Long commentId = getComment().getId();
        getReComment(commentId);
        getReComment(commentId);
        getReComment(commentId);
        getReComment(commentId);

    }*/





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


    private Post getPost(){
        Post post =  Post.builder()
                .category(PostCategory.FREE)
                .author("author")
                .title("title")
                .content("content")
                .isAnonymous(0)
                .build();
        return postRepository.save(post);
    }

    private CommentRequest getCommentRequest(String content){
        return CommentRequest.builder()
                .authorName("author")
                .content(content)
                .isAnonymous(0)
                .build();
    }

    private Comment getComment(){
        return Comment.builder()
                .author("author")
                .content("content")
                .build();
    }

    private Comment getReComment(Long parent){
        return Comment.builder()
                .parent(commentService.findCommentById(parent))
                .author("author")
                .content("content")
                .build();
    }
}
