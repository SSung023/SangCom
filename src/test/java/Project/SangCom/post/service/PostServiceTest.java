package Project.SangCom.post.service;

import Project.SangCom.post.domain.Post;
import Project.SangCom.post.domain.PostCategory;
import Project.SangCom.post.dto.PostRequest;
import Project.SangCom.post.dto.PostResponse;
import Project.SangCom.post.repository.PostRepository;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.repository.UserRepository;
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ErrorCode;
import Project.SangCom.utils.WithMockCustomUser;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
@Slf4j
public class PostServiceTest {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostService service;



    @Test
    @DisplayName("PostRequest 객체를 Post로 변환할 수 있다.")
    public void convertToPost(){
        //given
        PostRequest postRequest = getPostRequest("content");

        //when
        Post receivedPost = postRequest.toEntity(PostCategory.FREE);
        Post savedPost = postRepository.save(receivedPost);

        //then
        Assertions.assertThat(receivedPost).isEqualTo(savedPost);
    }

    @Test
    @DisplayName("Post 객체를 PostResponse 객체로 변환할 수 있다.")
    @WithMockCustomUser
    public void convertToPostResponse(){
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Post post = Post.builder()
                .category(PostCategory.FREE)
                .author("author")
                .title("title")
                .content("content")
                .isAnonymous(0)
                .build();
        post.updateLikes(1);

        //when
        Post savedPost = postRepository.save(post);
        savedPost.addUser(user);
        PostResponse postDetailResponse = service.convertToDetailResponse(user, savedPost);

        //then
        Assertions.assertThat(postDetailResponse.getId()).isEqualTo(savedPost.getId());
        Assertions.assertThat(postDetailResponse.getAuthor()).isEqualTo(savedPost.getAuthor());
        Assertions.assertThat(postDetailResponse.getTitle()).isEqualTo(savedPost.getTitle());
        Assertions.assertThat(postDetailResponse.getContent()).isEqualTo(savedPost.getContent());
        Assertions.assertThat(postDetailResponse.getBoardCategory()).isEqualTo("FREE");
        Assertions.assertThat(postDetailResponse.getLikeCount()).isEqualTo(savedPost.getLikeCount());
        Assertions.assertThat(postDetailResponse.getIsAnonymous()).isEqualTo(0);
    }

    @Test
    @DisplayName("PostRequest 객체를 전달받아 service를 통해 게시글을 DB에 저장할 수 있다.")
    @WithMockCustomUser
    public void canSavePost(){
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PostRequest request = getPostRequest("content");

        //when
        Long registeredId = service.savePost(user.getId(), PostCategory.FREE, request);
        Post savedPost = postRepository.findById(registeredId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));

        //then
        Assertions.assertThat(request.getAuthorNickname()).isEqualTo(savedPost.getAuthor());
        Assertions.assertThat(request.getTitle()).isEqualTo(savedPost.getTitle());
        Assertions.assertThat(request.getContent()).isEqualTo(savedPost.getContent());
        Assertions.assertThat(PostCategory.FREE.toString()).isEqualTo(savedPost.getCategory().toString());
        Assertions.assertThat(request.getIsAnonymous()).isEqualTo(savedPost.getIsAnonymous());
    }

    @Test
    @DisplayName("특정 게시글을 checkIsPostOwner를 통해 로그인한 사용자가 작성한 게시글인지 확인할 수 있다.")
    @WithMockCustomUser(nickname = "nickname")
    public void checkIsPostOwner(){
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PostRequest request = getPostRequest("content");

        //when
        Long savedId = service.savePost(user.getId(), PostCategory.FREE, request); // 게시글 등록

        PostResponse postDetailResponse = service.convertToDetailResponse(user, savedId);

        //then
        Assertions.assertThat(postDetailResponse.getIsOwner()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("특정 사용자가 작성한 게시글이라면 응답 시, 응답 객체의 isOwner가 1(true)여야 한다.")
    @WithMockCustomUser(nickname = "nickname")
    public void isOwnerIs1_whenUserIsWriter(){
        //given
        PostRequest request = getPostRequest("content");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        //when
        Long savedId = service.savePost(user.getId(), PostCategory.FREE, request); // 게시글 등록
        PostResponse postDetailResponse = service.convertToDetailResponse(user, savedId);

        //then
        Assertions.assertThat(postDetailResponse.getId()).isEqualTo(savedId);
        Assertions.assertThat(postDetailResponse.getAuthor()).isEqualTo("익명");
        Assertions.assertThat(postDetailResponse.getTitle()).isEqualTo(request.getTitle());
        Assertions.assertThat(postDetailResponse.getContent()).isEqualTo(request.getContent());
        Assertions.assertThat(postDetailResponse.getIsAnonymous()).isEqualTo(request.getIsAnonymous());

        Assertions.assertThat(postDetailResponse.getIsOwner()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("익명으로 작성한 게시글일 때 응답 객체의 author에 익명으로 전달되어야 한다.")
    @WithMockCustomUser
    public void passAsAnonymous(){
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Post post = Post.builder()
                .category(PostCategory.FREE)
                .title("title")
                .content("content")
                .author("nickname1")
                .isAnonymous(1)
                .build();
        
        //when
        Post savedPost = postRepository.save(post);
        savedPost.addUser(user);
        PostResponse postDetailResponse = service.convertToDetailResponse(user, savedPost);

        //then
        Assertions.assertThat(postDetailResponse.getAuthor()).isEqualTo("익명");
    }

    @Test
    @DisplayName("실명으로 작성한 게시글일 때 응답 객체의 author에 닉네임으로 전달되어야 한다.")
    @WithMockCustomUser
    public void passAsNickname(){
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Post post = Post.builder()
                .category(PostCategory.FREE)
                .title("title")
                .content("content")
                .author("nickname1")
                .isAnonymous(0)
                .build();

        //when
        Post savedPost = postRepository.save(post);
        savedPost.addUser(user);
        PostResponse postDetailResponse = service.convertToDetailResponse(user, savedPost);

        //then
        Assertions.assertThat(postDetailResponse.getAuthor()).isEqualTo(post.getAuthor());
    }
    
    @Test
    @DisplayName("postId(PK)를 통해 특정 게시글을 조회할 수 있다.")
    public void canLookupPostById(){
        //given
        User user = getUser();
        PostRequest request = getPostRequest("content");
        
        //when
        Long savedId = service.savePost(user.getId(), PostCategory.FREE, request); // 게시글 저장
        Post postById = service.findPostById(savedId); // postId(PK)를 통해 특정 게시글 조회
        
        //then
        Assertions.assertThat(request.getAuthorNickname()).isEqualTo(postById.getAuthor());
        Assertions.assertThat(request.getTitle()).isEqualTo(postById.getTitle());
        Assertions.assertThat(request.getContent()).isEqualTo(postById.getContent());
        Assertions.assertThat(PostCategory.FREE.toString()).isEqualTo(postById.getCategory().toString());
        Assertions.assertThat(request.getIsAnonymous()).isEqualTo(postById.getIsAnonymous());
    }

    @Test
    @DisplayName("postId(PK)를 통해 조회한 특정 게시글을 필드가 모두 유의미한 PostResponse 객체로 변환할 수 있다.")
    @WithMockCustomUser
    public void convertPostToPostResponse(){
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PostRequest request = getPostRequest("content");

        //when
        Long savedId = service.savePost(user.getId(), PostCategory.FREE, request); // 게시글 저장
        Post postById = service.findPostById(savedId); // postId(PK)를 통해 특정 게시글 조회

        PostResponse postDetailResponse = service.convertToDetailResponse(user, postById); // 조회한 게시글을 Response 객체로 변환

        //then
        Assertions.assertThat(postDetailResponse.getId()).isEqualTo(postById.getId());
        Assertions.assertThat(postDetailResponse.getAuthor()).isEqualTo("익명");
        Assertions.assertThat(postDetailResponse.getTitle()).isEqualTo(postById.getTitle());
        Assertions.assertThat(postDetailResponse.getContent()).isEqualTo(postById.getContent());
        Assertions.assertThat(postDetailResponse.getBoardCategory()).isEqualTo(postById.getCategory().toString());
        Assertions.assertThat(postDetailResponse.getCommentCount()).isEqualTo(postById.getComments().size());
        Assertions.assertThat(postDetailResponse.getLikeCount()).isEqualTo(0);
        Assertions.assertThat(postDetailResponse.getIsLikePressed()).isEqualTo(0);
        Assertions.assertThat(postDetailResponse.getIsOwner()).isEqualTo(1);
        Assertions.assertThat(postDetailResponse.getCreatedDate()).isEqualTo(postById.getCreatedDate());
        Assertions.assertThat(postDetailResponse.getIsAnonymous()).isEqualTo(postById.getIsAnonymous());
    }

    @Test
    @DisplayName("postId(PK)를 통해 조회한 특정 게시글을 특정 필드민 유의미한 PostResponse 객체로 변환할 수 있다.")
    @WithMockCustomUser
    public void convertPostToPreviewPostResponse(){
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PostRequest request = getPostRequest("content");

        //when
        Long savedId = service.savePost(user.getId(), PostCategory.FREE, request); // 게시글 저장
        Post postById = service.findPostById(savedId); // postId(PK)를 통해 특정 게시글 조회

        PostResponse postDetailResponse = service.convertToPreviewResponse(user, postById); // 조회한 게시글을 Response 객체로 변환

        //then
        Assertions.assertThat(postDetailResponse.getId()).isEqualTo(postById.getId());
        Assertions.assertThat(postDetailResponse.getAuthor()).isEqualTo("익명");
        Assertions.assertThat(postDetailResponse.getTitle()).isEqualTo(postById.getTitle());
        Assertions.assertThat(postDetailResponse.getContent()).isEqualTo(postById.getContent());
        Assertions.assertThat(postDetailResponse.getCommentCount()).isEqualTo(postById.getComments().size());
        Assertions.assertThat(postDetailResponse.getLikeCount()).isEqualTo(0);
        Assertions.assertThat(postDetailResponse.getIsLikePressed()).isEqualTo(0);
        Assertions.assertThat(postDetailResponse.getCreatedDate()).isEqualTo(postById.getCreatedDate());
    }

    @Test
    @DisplayName("postId(PK)를 통해 조회한 특정 게시글의 내용을 수정할 수 있다.")
    public void UpdateCertainPost(){
        //given
        User user = getUser();
        PostRequest postRequest = getPostRequest("content");
        PostRequest newRequest = getPostRequest("new-content");

        //when
        User save = userRepository.save(user);
        Long savedId = service.savePost(save.getId(), PostCategory.FREE, postRequest);
        Long modifiedPostId = service.updatePost(savedId, newRequest);

        Post post = service.findPostById(savedId);
        Post modifiedPost = service.findPostById(modifiedPostId);

        //then
        //post와 modifiedPost의 필드값들이 같아야 한다
        Assertions.assertThat(post.getAuthor()).isEqualTo(modifiedPost.getAuthor());
        Assertions.assertThat(post.getCategory()).isEqualTo(modifiedPost.getCategory());
        Assertions.assertThat(post.getTitle()).isEqualTo(modifiedPost.getTitle());

        Assertions.assertThat(post.getContent()).isEqualTo(modifiedPost.getContent());
        Assertions.assertThat(modifiedPost.getContent()).isEqualTo("new-content");
    }

    @Test
    @DisplayName("postId(PK)를 통해 특정 게시글을 삭제 처리하면 isDeleted가 true가 된다.")
    public void deletePost(){
        //given
        User user = getUser();
        PostRequest postRequest = getPostRequest("content");

        //when
        User save = userRepository.save(user);
        Long savedId = service.savePost(save.getId(), PostCategory.FREE, postRequest);
        Long deletePostId = service.deletePost(savedId);

        Post deletedPost = service.findPostById(deletePostId);

        //then
        Assertions.assertThat(deletedPost.getIsDeleted()).isEqualTo(1);
    }

    @Test
    @DisplayName("FREE 자유게시판의 삭제되지 않은 게시글을 조회할 수 있다.")
    @WithMockCustomUser
    public void pagingNotDeletedPosts(){
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Post post1 = getPost(PostCategory.FREE, "title", "content", 0);
        Post post2 = getPost(PostCategory.FREE, "title", "content", 1);
        Post post3 = getPost(PostCategory.GRADE1, "title", "content", 0);
        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        //when
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));
        Slice<PostResponse> postList = service.getNotDeletedPostList(user, PostCategory.FREE, pageRequest);

        //then
        Assertions.assertThat(postList.getContent().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("FREE 자유게시판에서 제목을 통해 게시글을 검색할 수 있다.")
    @WithMockCustomUser
    public void searchPostByTitleInFree(){
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Post post1 = getPost(PostCategory.FREE, "keyword", "content", 0);
        Post post2 = getPost(PostCategory.FREE, "title", "keyword", 1);
        Post post3 = getPost(PostCategory.GRADE1, "title2", "keyword", 0);
        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        //when
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));
        Slice<PostResponse> postList = service.searchPosts(user, "title", "keyword", PostCategory.FREE, pageRequest);

        //then
        Assertions.assertThat(postList.getContent().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("FREE 자유게시판에서 내용을 통해 게시글을 검색할 수 있다.")
    @WithMockCustomUser
    public void searchPostByContentInFree(){
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Post post1 = getPost(PostCategory.FREE, "keyword", "content", 0);
        Post post2 = getPost(PostCategory.FREE, "title", "keyword", 1);
        Post post3 = getPost(PostCategory.FREE, "title2", "keyword", 0);
        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        //when
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));
        Slice<PostResponse> postList = service.searchPosts(user, "content", "keyword", PostCategory.FREE, pageRequest);

        //then
        Assertions.assertThat(postList.getContent().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("내가 쓴 글을 모두 조회할 수 있다")
    @WithMockCustomUser
    public void canFindAllWritePost(){
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PostRequest postRequest1 = getPostRequest("content1");
        PostRequest postRequest2 = getPostRequest("content2");
        PostRequest postRequest3 = getPostRequest("content3");
        PostRequest postRequest4 = getPostRequest( "content4");

        service.savePost(user.getId(), PostCategory.FREE, postRequest1);
        service.savePost(user.getId(), PostCategory.FREE, postRequest2);
        service.savePost(user.getId(), PostCategory.FREE, postRequest3);
        service.savePost(user.getId(), PostCategory.FREE, postRequest4);

        //when
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"));
        Slice<PostResponse> wrotePost = service.getAllWritePostList(user, pageRequest);

        //then
        Assertions.assertThat(wrotePost.getContent().size()).isEqualTo(4);
    }

    @Test
    @DisplayName("특정 게시판에서 최근에 작성한 게시글 5개를 불러올 수 있다.")
    public void canGetRecentPosts(){
        //given
        User user = getUser();

        Post post1 = getPost("title1", "content1", PostCategory.FREE, 0);
        Post post2 = getPost("title2", "content2", PostCategory.FREE, 0);
        Post post3 = getPost("title3", "content3", PostCategory.FREE, 0);
        Post post4 = getPost("title4", "content4", PostCategory.FREE, 1);
        Post post5 = getPost("title5", "content5", PostCategory.FREE, 0);
        Post post6 = getPost("title6", "content6", PostCategory.FREE, 0);
        Post post7 = getPost("title7", "content7", PostCategory.GRADE1, 0);

        //when
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"));
        List<PostResponse> previewPosts = service.getPreviewPosts(user, PostCategory.FREE, pageRequest);

        //then
        Assertions.assertThat(previewPosts.size()).isEqualTo(5);

    }







    private Post getPost(String title, String content, PostCategory category, int isDeleted) {
        Post post = Post.builder()
                .title(title)
                .content(content)
                .category(category)
                .isDeleted(isDeleted)
                .build();
        return postRepository.save(post);
    }
    private User getUser() {
        User user = User.builder()
                .username("username")
                .nickname("nickname")
                .email("test@naver.com")
                .role(Role.STUDENT.getKey())
                .build();
        return userRepository.save(user);
    }
    private Post getPost(PostCategory category, String title, String content, int isDeleted) {
        return Post.builder()
                .title(title)
                .content(content)
                .category(category)
                .isDeleted(isDeleted)
                .build();
    }
    private PostRequest getPostRequest(String content) {
        return PostRequest.builder()
                .authorNickname("nickname")
                .isAnonymous(1) // true
                .title("title")
                .content(content)
                .build();
    }
}
