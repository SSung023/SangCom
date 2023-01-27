package Project.SangCom.post.service;

import Project.SangCom.post.domain.Post;
import Project.SangCom.post.domain.PostCategory;
import Project.SangCom.post.dto.PostRequest;
import Project.SangCom.post.dto.PostResponse;
import Project.SangCom.post.repository.PostRepository;
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ErrorCode;
import Project.SangCom.utils.WithMockCustomUser;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
public class PostServiceTest {

    @Autowired
    private PostRepository repository;
    @Autowired
    private PostService service;



    @Test
    @DisplayName("PostRequest 객체를 Post로 변환할 수 있다.")
    public void convertToPost(){
        //given
        PostRequest postRequest = getPostRequest("content");

        //when
        Post receivedPost = postRequest.toEntity();
        Post savedPost = repository.save(receivedPost);

        //then
        Assertions.assertThat(receivedPost).isEqualTo(savedPost);
    }

    @Test
    @DisplayName("Post 객체를 PostResponse 객체로 변환할 수 있다.")
    public void convertToPostResponse(){
        //given
        Post post = Post.builder()
                .category(PostCategory.FREE)
                .author("author")
                .title("title")
                .content("content")
                .isAnonymous(0)
                .build();

        //when
        Post savedPost = repository.save(post);
        PostResponse postResponse = service.convertToResponse(savedPost.getId());

        //then
        Assertions.assertThat(postResponse.getId()).isEqualTo(savedPost.getId());
        Assertions.assertThat(postResponse.getAuthor()).isEqualTo(savedPost.getAuthor());
        Assertions.assertThat(postResponse.getTitle()).isEqualTo(savedPost.getTitle());
        Assertions.assertThat(postResponse.getContent()).isEqualTo(savedPost.getContent());
        Assertions.assertThat(postResponse.getBoardCategory()).isEqualTo("FREE");
        Assertions.assertThat(postResponse.getIsAnonymous()).isEqualTo(0);
    }

    @Test
    @DisplayName("PostRequest 객체를 전달받아 service를 통해 게시글을 DB에 저장할 수 있다.")
    public void canSavePost(){
        //given
        PostRequest request = getPostRequest("content");

        //when
        Long registeredId = service.savePost(request);
        Post savedPost = repository.findById(registeredId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));

        //then
        Assertions.assertThat(request.getAuthorNickname()).isEqualTo(savedPost.getAuthor());
        Assertions.assertThat(request.getTitle()).isEqualTo(savedPost.getTitle());
        Assertions.assertThat(request.getContent()).isEqualTo(savedPost.getContent());
        Assertions.assertThat(request.getBoardCategory()).isEqualTo(savedPost.getCategory().toString());
        Assertions.assertThat(request.getIsAnonymous()).isEqualTo(savedPost.getIsAnonymous());
    }

    @Test
    @DisplayName("특정 게시글을 checkIsPostOwner를 통해 로그인한 사용자가 작성한 게시글인지 확인할 수 있다.")
    @WithMockCustomUser(nickname = "nickname")
    public void checkIsPostOwner(){
        //given
        PostRequest request = getPostRequest("content");

        //when
        Long savedId = service.savePost(request); // 게시글 등록

        PostResponse postResponse = service.convertToResponse(savedId);
        service.checkAndSetIsPostOwner(savedId, postResponse);

        //then
        Assertions.assertThat(postResponse.getIsOwner()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("특정 사용자가 작성한 게시글이라면 응답 시, 응답 객체의 isOwner가 1(true)여야 한다.")
    @WithMockCustomUser(nickname = "nickname")
    public void isOwnerIs1_whenUserIsWriter(){
        //given
        PostRequest request = getPostRequest("content");
        
        //when
        Long savedId = service.savePost(request); // 게시글 등록
        PostResponse postResponse = service.convertToResponse(savedId);

        service.checkAndSetIsPostOwner(savedId, postResponse);

        //then
        Assertions.assertThat(postResponse.getId()).isEqualTo(savedId);
        Assertions.assertThat(postResponse.getAuthor()).isEqualTo(request.getAuthorNickname());
        Assertions.assertThat(postResponse.getTitle()).isEqualTo(request.getTitle());
        Assertions.assertThat(postResponse.getContent()).isEqualTo(request.getContent());
        Assertions.assertThat(postResponse.getIsAnonymous()).isEqualTo(request.getIsAnonymous());

        Assertions.assertThat(postResponse.getIsOwner()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("postId(PK)를 통해 특정 게시글을 조회할 수 있다.")
    public void canLookupPostById(){
        //given
        PostRequest request = getPostRequest("content");
        
        //when
        Long savedId = service.savePost(request); // 게시글 저장
        Post postById = service.findPostById(savedId); // postId(PK)를 통해 특정 게시글 조회
        
        //then
        Assertions.assertThat(request.getAuthorNickname()).isEqualTo(postById.getAuthor());
        Assertions.assertThat(request.getTitle()).isEqualTo(postById.getTitle());
        Assertions.assertThat(request.getContent()).isEqualTo(postById.getContent());
        Assertions.assertThat(request.getBoardCategory()).isEqualTo(postById.getCategory().toString());
        Assertions.assertThat(request.getIsAnonymous()).isEqualTo(postById.getIsAnonymous());
    }

    @Test
    @DisplayName("postId(PK)를 통해 조회한 특정 게시글을 PostResponse 객체로 변환할 수 있다.")
    public void convertPostToPostResponse(){
        //given
        PostRequest request = getPostRequest("content");

        //when
        Long savedId = service.savePost(request); // 게시글 저장
        Post postById = service.findPostById(savedId); // postId(PK)를 통해 특정 게시글 조회

        PostResponse postResponse = service.convertToResponse(postById.getId()); // 조회한 게시글을 Response 객체로 변환

        //then
        Assertions.assertThat(postResponse.getId()).isEqualTo(postById.getId());
        Assertions.assertThat(postResponse.getAuthor()).isEqualTo(postById.getAuthor());
        Assertions.assertThat(postResponse.getTitle()).isEqualTo(postById.getTitle());
        Assertions.assertThat(postResponse.getContent()).isEqualTo(postById.getContent());
        Assertions.assertThat(postResponse.getBoardCategory()).isEqualTo(postById.getCategory().toString());
        Assertions.assertThat(postResponse.getIsAnonymous()).isEqualTo(postById.getIsAnonymous());
    }

    @Test
    @DisplayName("postId(PK)를 통해 조회한 특정 게시글의 내용을 수정할 수 있다.")
    public void UpdateCertainPost(){
        //given
        PostRequest postRequest = getPostRequest("content");
        PostRequest newRequest = getPostRequest("new-content");

        //when
        Long savedId = service.savePost(postRequest);
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

        log.info("post: " + post.toString());
        log.info("modifiedPost: " + modifiedPost.toString());
    }

    @Test
    @DisplayName("postId(PK)를 통해 특정 게시글을 삭제 처리하면 isDeleted가 true가 된다.")
    public void deletePost(){
        //given
        PostRequest postRequest = getPostRequest("content");

        //when
        Long savedId = service.savePost(postRequest);
        Long deletePostId = service.deletePost(savedId);

        Post deletedPost = service.findPostById(deletePostId);

        //then
        Assertions.assertThat(deletedPost.getIsDeleted()).isEqualTo(1);
    }
    
    



    private PostRequest getPostRequest(String content) {
        return PostRequest.builder()
                .authorNickname("nickname")
                .isAnonymous(1) // true
                .title("title")
                .content(content)
                .boardCategory("FREE")
                .build();
    }
}
