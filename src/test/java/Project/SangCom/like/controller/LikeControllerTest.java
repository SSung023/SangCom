package Project.SangCom.like.controller;

import Project.SangCom.comment.domain.Comment;
import Project.SangCom.comment.dto.CommentRequest;
import Project.SangCom.comment.service.CommentService;
import Project.SangCom.like.service.LikeService;
import Project.SangCom.post.domain.PostCategory;
import Project.SangCom.post.dto.PostRequest;
import Project.SangCom.post.service.PostService;
import Project.SangCom.security.dto.AccessTokenUserRequest;
import Project.SangCom.security.service.JwtTokenProvider;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.service.UserService;
import Project.SangCom.utils.WithMockCustomUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static Project.SangCom.security.service.JwtTokenProvider.AUTHORIZATION_HEADER;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@Transactional
@Slf4j
class LikeControllerTest {
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext context;
    @Autowired
    JwtTokenProvider provider;
    @Autowired
    UserService userService;
    @Autowired
    PostService postService;
    @Autowired
    CommentService commentService;
    @Autowired
    LikeService likeService;



    @BeforeEach
    public void setup(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }



    @Test
    @DisplayName("NOT_VERIFIED 권한의 유저는 좋아요 관련 api에 접근하지 못한다.")
    @WithMockCustomUser(role = Role.NOT_VERIFIED)
    public void notVerified_cannotAccess() throws Exception {
        //given
        String accessToken = getAccessToken();

        //when & then
        mockMvc.perform(post("/api/like/board")
                        .header(AUTHORIZATION_HEADER, accessToken))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("사용자 정보와 게시글 정보를 전달하여 특정 게시글에 좋아요 처리를 할 수 있다.")
    @WithMockCustomUser(role = Role.STUDENT)
    public void likePost() throws Exception {
        //given
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long savePostId = setPostAndSave(writer.getId());
        String accessToken = getAccessToken();

        String requestJson = "{\"postId\":\"" + savePostId + "\"}";

        //when & then
        mockMvc.perform(post("/api/like/board")
                .header(AUTHORIZATION_HEADER, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(savePostId));
    }

    @Test
    @DisplayName("특정 게시글의 좋아요 버튼을 눌러서 좋아요 취소 처리를 할 수 있다.")
    @WithMockCustomUser(role = Role.STUDENT)
    public void unlikePost() throws Exception{
        //given
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long savePostId = setPostAndSave(writer.getId());
        String accessToken = getAccessToken();

        String requestJson = "{\"postId\":\"" + savePostId + "\"}";

        //when
        likeService.toggleLikePost(writer.getId(), savePostId);

        //then
        mockMvc.perform(post("/api/like/board")
                .header(AUTHORIZATION_HEADER, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(savePostId))
                .andExpect(jsonPath("$.data.likeCount").value(0));
    }


    //=== 댓글 테스트 ===//
    @Test
    @DisplayName("댓글 좋아요")
    @WithMockCustomUser(role = Role.STUDENT)
    public void likeComment() throws Exception {
        //given
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long postId = setPostAndSave(writer.getId());

        Long saveCommentId = setCommentAndSave(writer.getId(), postId);
        String accessToken = getAccessToken();

        String requestJson = "{\"commentId\":\"" + saveCommentId + "\"}";

        //when & then
        Comment comment = commentService.findCommentById(saveCommentId);

        mockMvc.perform(post("/api/like/board/comment")
                        .header(AUTHORIZATION_HEADER, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(saveCommentId))
                .andExpect(jsonPath("$.data.authorName").value(comment.getAuthor()))
                .andExpect(jsonPath("$.data.content").value(comment.getContent()))
                .andExpect(jsonPath("$.data.likeCount").value(comment.getLikeCount()));
    }

    @Test
    @DisplayName("댓글 좋아요 취소")
    @WithMockCustomUser(role = Role.STUDENT)
    public void unlikeComment() throws Exception{
        //given
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long postId = setPostAndSave(writer.getId());

        Long saveCommentId = setCommentAndSave(writer.getId(), postId);
        String accessToken = getAccessToken();

        String requestJson = "{\"commentId\":\"" + saveCommentId + "\"}";

        //when
        likeService.toggleLikeComment(writer.getId(), saveCommentId);

        //then
        Comment comment = commentService.findCommentById(saveCommentId);

        mockMvc.perform(post("/api/like/board/comment")
                        .header(AUTHORIZATION_HEADER, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(saveCommentId))
                .andExpect(jsonPath("$.data.authorName").value(comment.getAuthor()))
                .andExpect(jsonPath("$.data.content").value(comment.getContent()))
                .andExpect(jsonPath("$.data.likeCount").value(comment.getLikeCount()));
    }

    @Test
    @DisplayName("대댓글 좋아요")
    @WithMockCustomUser(role = Role.STUDENT)
    public void likeReComment() throws Exception {
        //given
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long postId = setPostAndSave(writer.getId());
        Long parentId = setCommentAndSave(writer.getId(), postId);
        Long saveReCommentId = setReCommentAndSave(writer.getId(), postId, parentId);
        String accessToken = getAccessToken();

        String requestJson = "{\"commentId\":\"" + saveReCommentId + "\", \"parentId\":\"" + parentId + "\"}";

        //when & then
        Comment reComment = commentService.findCommentById(saveReCommentId);

        mockMvc.perform(post("/api/like/board/comment")
                        .header(AUTHORIZATION_HEADER, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(reComment.getId()))
                .andExpect(jsonPath("$.data.authorName").value(reComment.getAuthor()))
                .andExpect(jsonPath("$.data.content").value(reComment.getContent()))
                .andExpect(jsonPath("$.data.likeCount").value(reComment.getLikeCount()));
    }

    @Test
    @DisplayName("대댓글 좋아요 취소")
    @WithMockCustomUser(role = Role.STUDENT)
    public void unlikeReComment() throws Exception{
        //given
        User writer = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long postId = setPostAndSave(writer.getId());
        Long parentId = setCommentAndSave(writer.getId(), postId);
        Long saveReCommentId = setReCommentAndSave(writer.getId(), postId, parentId);
        String accessToken = getAccessToken();

        String requestJson = "{\"commentId\":\"" + saveReCommentId + "\", \"parentId\":\"" + parentId + "\"}";

        //when
        likeService.toggleLikeComment(writer.getId(), saveReCommentId);

        //then
        Comment reComment = commentService.findCommentById(saveReCommentId);

        mockMvc.perform(post("/api/like/board/comment")
                        .header(AUTHORIZATION_HEADER, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(reComment.getId()))
                .andExpect(jsonPath("$.data.authorName").value(reComment.getAuthor()))
                .andExpect(jsonPath("$.data.content").value(reComment.getContent()))
                .andExpect(jsonPath("$.data.likeCount").value(reComment.getLikeCount()));
    }





    private User getUser() {
        return User.builder()
                .username("username")
                .nickname("nickname")
                .email("test@naver.com")
                .role(Role.NOT_VERIFIED)
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
    private Long setUserAndSave(){
        User user = User.builder()
                .role(Role.STUDENT)
                .email("test@naver.com")
                .nickname("nickname")
                .username("username")
                .build();
        return userService.saveUser(user);
    }
    private Long setPostAndSave(Long saveUserId){
        PostRequest postRequest = PostRequest.builder()
                .authorNickname("authorNickname")
                .title("title1")
                .content("content1")
                .isAnonymous(0)
                .build();
        return postService.savePost(saveUserId, PostCategory.FREE, postRequest);
    }

    private Long setCommentAndSave(Long saveUserId, Long savePostId){
        CommentRequest commentRequest = CommentRequest.builder()
                .authorName("author")
                .content("comment content")
                .isAnonymous(0)
                .build();
        return commentService.saveComment(saveUserId, savePostId, commentRequest);
    }

    private Long setReCommentAndSave(Long saveUserId, Long savePostId, Long saveParentId){
        CommentRequest commentRequest = CommentRequest.builder()
                //.parentId(saveParentId)
                .authorName("author")
                .content("comment content")
                .isAnonymous(0)
                .build();
        return commentService.saveReComment(saveUserId, savePostId, saveParentId, commentRequest);
    }
}