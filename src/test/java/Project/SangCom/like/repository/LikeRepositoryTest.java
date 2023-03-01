package Project.SangCom.like.repository;

import Project.SangCom.comment.domain.Comment;
import Project.SangCom.comment.dto.CommentRequest;
import Project.SangCom.comment.repository.CommentRepository;
import Project.SangCom.comment.service.CommentService;
import Project.SangCom.like.domain.Likes;
import Project.SangCom.post.domain.Post;
import Project.SangCom.post.domain.PostCategory;
import Project.SangCom.post.dto.PostRequest;
import Project.SangCom.post.repository.PostRepository;
import Project.SangCom.post.service.PostService;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.repository.UserRepository;
import Project.SangCom.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
class LikeRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    //Autowired
    //private CommentService commentService;
    @Autowired
    private LikeRepository likeRepository;

    @Test
    @DisplayName("Like 객체를 save하고 찾을 수 있다.")
    public void likeTest(){
        //given
        Likes likes = new Likes();

        //when
        Likes savedLike = likeRepository.save(likes);

        //then
        Assertions.assertThat(savedLike).isEqualTo(likeRepository.findById(savedLike.getId()).get());
    }

    @Test
    @DisplayName("userId와 postId를 통해 Likes 객체를 찾을 수 있다.")
    public void findLike(){
        //given
        Long userId = setUserAndSave("test@naver.com", "nickname");
        Long postId = setPostAndSave(userId);

        //when
        Likes likes = setLikes(userId, postRepository.findById(postId));

        likeRepository.save(likes);
        Likes foundLike = likeRepository.findLikes(userId, postId).get();

        //then
        Assertions.assertThat(foundLike.getUser().getId()).isEqualTo(userId);
        Assertions.assertThat(foundLike.getPost().getId()).isEqualTo(postId);
    }

    @Test
    @DisplayName("저장되어 있는 Likes 객체를 삭제할 수 있다.")
    public void deleteLike(){
        //given
        Long userId = setUserAndSave("test@naver.com", "nickname");
        Long postId = setPostAndSave(userId);

        //when
        Likes likes = setLikes(userId, postRepository.findById(postId));

        Likes savedLike = likeRepository.save(likes);
        likeRepository.delete(savedLike);

        //then
        Assertions.assertThat(likeRepository.findById(savedLike.getId())).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("일정 시간 내에 좋아요 수가 제일 높은 게시글을 얻을 수 있다.")
    public void canGetMostLikePost(){
        //given
        Long userId1 = setUserAndSave("test1@naver.com", "nickname1");
        Long userId2 = setUserAndSave("test2@naver.com", "nickname2");
        Long userId3 = setUserAndSave("test3@naver.com", "nickname3");

        Long postId1 = setPostAndSave(userId1);
        Long postId2 = setPostAndSave(userId1);
        Long postId3 = setPostAndSave(userId2);

        //when
        Likes likes1 = setLikes(userId1, postRepository.findById(postId1));
        Likes likes2 = setLikes(userId2, postRepository.findById(postId1));
        Likes likes3 = setLikes(userId3, postRepository.findById(postId1));
        postRepository.findById(postId1).get().updateLikes(3);

        Likes likes4 = setLikes(userId1, postRepository.findById(postId2));
        Likes likes5 = setLikes(userId2, postRepository.findById(postId2));
        postRepository.findById(postId2).get().updateLikes(2);

        Likes likes6 = setLikes(userId1, postRepository.findById(postId3));
        postRepository.findById(postId3).get().updateLikes(1);

        likeRepository.save(likes1);
        likeRepository.save(likes2);
        likeRepository.save(likes3);
        likeRepository.save(likes4);
        likeRepository.save(likes5);
        likeRepository.save(likes6);

        //then
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id"));
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);

        List<Post> mostLikedPost = postRepository.findMostLikedPost(threshold, PostCategory.FREE, pageRequest);
        Post post = mostLikedPost.get(0);

        Assertions.assertThat(post.getLikeCount()).isEqualTo(3);
    }

    //=== 댓글 테스트 ===//

    @Test
    @DisplayName("userId와 commentId를 통해 Likes 객체를 찾을 수 있다.")
    public void findCommentLike(){
        //given
        Long userId = setUserAndSave("test@naver.com", "nickname");
        Long postId = setPostAndSave(userId);
        Long commentId = setCommentAndSave(userId, postId);

        //when
        Likes likes = setCommentLikes(userId, postId, commentRepository.findById(commentId));

        likeRepository.save(likes);
        Likes foundLike = likeRepository.findCommentLikes(userId, commentId).get();

        //then
        Assertions.assertThat(foundLike.getUser().getId()).isEqualTo(userId);
        Assertions.assertThat(foundLike.getPost().getId()).isEqualTo(postId);
        Assertions.assertThat(foundLike.getComment().getId()).isEqualTo(commentId);
    }

    @Test
    @DisplayName("저장된 댓글 좋아요 삭제")
    public void deleteCommentLike(){
        //given
        Long userId = setUserAndSave("test@naver.com", "nickname");
        Long postId = setPostAndSave(userId);
        Long commentId = setCommentAndSave(userId, postId);

        //when
        Likes likes = setCommentLikes(userId, postId, commentRepository.findById(commentId));

        Likes savedLike = likeRepository.save(likes);
        likeRepository.delete(savedLike);

        //then
        Assertions.assertThat(likeRepository.findById(savedLike.getId())).isEqualTo(Optional.empty());
    }





    private Likes setLikes(Long userId, Optional<Post> optionalPost) {
        Likes likes = new Likes();
        likes.setUser(userRepository.findById(userId).get());
        likes.setPost(optionalPost.get());

        return likes;
    }

    private Likes setCommentLikes(Long userId, Long postId, Optional<Comment> optionalComment) {
        Likes likes = new Likes();
        likes.setUser(userRepository.findById(userId).get());
        likes.setPost(postRepository.findById(postId).get());
        likes.setComment(optionalComment.get());

        return likes;
    }

    private Long setUserAndSave(String email, String nickname){
        User user = User.builder()
                .role(Role.STUDENT.getKey())
                .email(email)
                .nickname(nickname)
                .username("username")
                .build();
        User save = userRepository.save(user);
        return save.getId();
    }
    private Long setPostAndSave(Long saveUserId){
        Post post = Post.builder()
                .category(PostCategory.FREE)
                .author("author")
                .title("title")
                .content("content")
                .build();
        post.addUser(userRepository.findById(saveUserId).get());
        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }

    private Long setCommentAndSave(Long saveUserId, Long savePostId){
        Comment comment = Comment.builder()
                .author("author")
                .content("comment content")
                .isAnonymous(0)
                .build();
        comment.setUser(userRepository.findById(saveUserId).get());
        comment.setPost(postRepository.findById(savePostId).get());
        Comment saveComment = commentRepository.save(comment);

        return saveComment.getId();
    }
}