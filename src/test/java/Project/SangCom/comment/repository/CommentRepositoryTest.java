package Project.SangCom.comment.repository;

import Project.SangCom.comment.domain.Comment;
import Project.SangCom.post.domain.Post;
import Project.SangCom.post.domain.PostCategory;
import Project.SangCom.post.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Slice;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
public class CommentRepositoryTest {
    @Autowired
    PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("Comment 객체 저장 테스트")
    public void repositorySaveTest(){
        //given
        Comment comment = getComment("name1", "content1", 0);

        //when
        Comment save = commentRepository.save(comment);
        log.info(comment.toString());

        //then
        Assertions.assertThat(comment).isEqualTo(commentRepository.findById(save.getId()).get());
    }

    @Test
    @DisplayName("특정 게시글의 댓글을 찾을 수 있다.")
    public void canFindComment(){
        //given
        Post post = getSavedPost();
        Comment comment1 = getSavedComment("name1", "content1", 0, post);
        Comment comment2 = getSavedComment("name2", "content2", 0, post);
        Comment comment3 = getSavedComment("name3", "content3", 0, post);

        //when
        List<Comment> postComment = commentRepository.findPostComment(post.getId());

        //then
        Assertions.assertThat(postComment.size()).isEqualTo(3);
        Assertions.assertThat(postComment).contains(comment1);
        Assertions.assertThat(postComment).contains(comment2);
        Assertions.assertThat(postComment).contains(comment3);

    }





    private Comment getComment(String author, String content, int isDeleted){
        return Comment.builder()
                .author(author)
                .content(content)
                .isDeleted(isDeleted)
                .isAnonymous(0)
                .build();
    }
    private Comment getSavedComment(String author, String content, int isDeleted, Post post){
        Comment comment = Comment.builder()
                .author(author)
                .content(content)
                .isDeleted(isDeleted)
                .isAnonymous(0)
                .build();
        comment = commentRepository.save(comment);
        comment.setPost(post);
        return comment;
    }
    private Post getSavedPost(){
        Post post = Post.builder()
                .title("title")
                .content("content")
                .isAnonymous(0)
                .category(PostCategory.FREE)
                .build();
        return postRepository.save(post);
    }
}
