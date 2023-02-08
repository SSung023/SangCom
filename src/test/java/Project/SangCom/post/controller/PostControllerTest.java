package Project.SangCom.post.controller;


import Project.SangCom.post.domain.Post;
import Project.SangCom.post.domain.PostCategory;
import Project.SangCom.post.dto.PostRequest;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static Project.SangCom.post.dto.PostResponse.*;
import static Project.SangCom.security.service.JwtTokenProvider.GRANT_HEADER;
import static Project.SangCom.security.service.JwtTokenProvider.REFRESH_HEADER;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@Slf4j
@ActiveProfiles({"jwt"})
class PostControllerTest {
    @Value("${jwt.secret}")
    String secretKey;
    @Value("${jwt.refresh-secret}")
    String refreshSecretKey;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    Long refreshTokenValidityInMilliseconds;
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext context;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
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
                "\"authorNickname\":\"nickname\", \"title\":\"title\", \"content\":\"content\"," +
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
                .andExpect(jsonPath("$.data.isOwner").value(TRUE))
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
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long savePostId = postService.savePost(writer, postRequest);

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
                .andExpect(jsonPath("$.data.isOwner").value(TRUE))
                .andExpect(jsonPath("$.data.isAnonymous").value("0"));
    }

    @Test
    @DisplayName("자유게시판에서 특정 글의 content(내용)을 수정할 수 있다.")
    @WithMockCustomUser(role = Role.STUDENT)
    public void updatePostContent() throws Exception {
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String accessToken = getAccessToken();
        PostRequest postRequest = getPostRequest("content");
        String requestJson = "{\"id\":\"\", \"boardCategory\":\"FREE\"," +
                "\"author\":\"nickname\", \"title\":\"title\", \"content\":\"new-content\"," +
                "\"isAnonymous\":\"0\"}";

        //when
        Long savePostId = postService.savePost(user, postRequest);

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
                .andExpect(jsonPath("$.data.isOwner").value(TRUE))
                .andExpect(jsonPath("$.data.isAnonymous").value("0"));

    }

    @Test
    @DisplayName("자유게시판에서 특정 글을 삭제 처리 할 수 있다.")
    @WithMockCustomUser
    public void deletePost() throws Exception {
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String accessToken = getAccessToken();
        PostRequest postRequest = getPostRequest("content");

        // when
        Long savePostId = postService.savePost(user, postRequest);

        //then
        mockMvc.perform(delete("/api/board/free/" + savePostId)
                .header(AUTHORIZATION_HEADER, accessToken))
                .andExpect(status().isOk());

    }
    
    @Test
    @DisplayName("자유게시판에서 글들을 조회할 수 있다.")
    @WithMockCustomUser(role = Role.STUDENT)
    public void getPosts() throws Exception {
        //given
        String accessToken = getAccessToken();
        Post post1 = getPost(PostCategory.FREE, "title1", "content1", 0);
        Post post2 = getPost(PostCategory.FREE, "title2", "content2", 0);
        Post post3 = getPost(PostCategory.FREE, "title3", "content3", 1);
        Post post4 = getPost(PostCategory.FREE, "title4", "content4", 0);

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);
        postRepository.save(post4);
        
        //when&then
        mockMvc.perform(get("/api/board/free/list")
                .header(AUTHORIZATION_HEADER, accessToken))
                .andExpect(jsonPath("$.status").value(SuccessCode.SUCCESS.getKey()))
                .andExpect(jsonPath("$.message").value(SuccessCode.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.pageable.pageNumber").value(0));
        
    }

    @Test
    @DisplayName("자유게시판에서 글을 페이징 형식으로 검색할 수 있다.")
    @WithMockCustomUser(role = Role.STUDENT)
    public void searchPost() throws Exception {
        //given
        String accessToken = getAccessToken();
        Post post1 = getPost(PostCategory.FREE, "title1", "content1", 0);
        Post post2 = getPost(PostCategory.FREE, "title2", "content2", 0);
        Post post3 = getPost(PostCategory.FREE, "title3", "content3", 1);
        Post post4 = getPost(PostCategory.FREE, "title4", "content4", 0);

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);
        postRepository.save(post4);

        //when&then
        mockMvc.perform(get("/api/board/free/search?query=title&keyword=title")
                .header(AUTHORIZATION_HEADER, accessToken))
                .andExpect(jsonPath("$.status").value(SuccessCode.SUCCESS.getKey()))
                .andExpect(jsonPath("$.message").value(SuccessCode.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.numberOfElements").value(3))
                .andExpect(jsonPath("$.data.pageable.offset").value(0))
                .andExpect(jsonPath("$.data.pageable.pageNumber").value(0));

    }

    @Test
    @DisplayName("자유게시판에서 글들을 조회할 때, Access-token이 유효하다면 Grant-Type이 auth-token이어야 한다.")
    @WithMockCustomUser(role = Role.STUDENT)
    public void checkGrantTypeHeader1() throws Exception {
        //given
        String accessToken = getShortAccessToken(1000000L);

        //when&then
        mockMvc.perform(get("/api/board/free/list")
                        .header(AUTHORIZATION_HEADER, accessToken))
                .andExpect(header().string(GRANT_HEADER, "auth-grant"));

    }

    @Test
    @DisplayName("자유게시판에서 글들을 조회할 때, Access-token이 유효하지 않다면 Grant-Type이 reissued-token이어야 한다.")
    @WithMockCustomUser(role = Role.STUDENT)
    public void checkGrantTypeHeader2() throws Exception {
        //given
        String accessToken = getShortAccessToken(10L);
        User user = getUser();
        AccessTokenUserRequest userDTO = AccessTokenUserRequest.builder()
                .email(user.getEmail())
                .role(user.getRole().getKey())
                .build();

        String refreshToken = provider.createRefreshToken(userDTO);
        ResponseCookie cookie
                = ResponseCookie.from("refreshToken", refreshToken)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .maxAge(6 * 60 * 60) // 6hours * 60min * 60sec
                .build();

        //when&then
        mockMvc.perform(get("/api/board/free/list")
                        .header(AUTHORIZATION_HEADER, accessToken)
                        .header(REFRESH_HEADER, cookie.toString()))
                .andExpect(header().string(GRANT_HEADER, "reissued-grant"));

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
    private String getShortAccessToken(Long duration){
        Long now = System.currentTimeMillis();
        User user = getUser();
        AccessTokenUserRequest userDTO = AccessTokenUserRequest.builder()
                .email(user.getEmail())
                .role(user.getRole().getKey())
                .build();

        String accessToken = Jwts.builder()
                .setHeader(createHeader())
                .setClaims(createClaims(userDTO))
                .setSubject(userDTO.getEmail())
                .setExpiration(new Date(now + duration))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
        return "Bearer " + accessToken;
    }
    private Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS512");
        return header;
    }
    private Map<String, Object> createClaims(AccessTokenUserRequest userDTO) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userDTO.getEmail());
        claims.put("role", userDTO.getRole());
        return claims;

    }
    private PostRequest getPostRequest(String content) {
        return PostRequest.builder()
                .boardCategory("FREE")
                .authorNickname("nickname")
                .title("title")
                .content(content)
                .isAnonymous(0)
                .build();
    }
    private Post getPost(PostCategory category, String title, String content, int isDeleted) {
        return Post.builder()
                .title(title)
                .content(content)
                .category(category)
                .isDeleted(isDeleted)
                .build();
    }
}