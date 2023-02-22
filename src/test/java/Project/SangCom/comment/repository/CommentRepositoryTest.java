package Project.SangCom.comment.repository;

import Project.SangCom.comment.domain.Comment;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository repository;

    @Test
    @DisplayName("Comment 객체 저장 테스트")
    public void repositorySaveTest(){
        //given
        Comment comment = getComment("name1", "content1", 0);

        //when
        Comment save = repository.save(comment);
        log.info(comment.toString());

        //then
        Assertions.assertThat(comment).isEqualTo(repository.findById(save.getId()).get());
    }


    private Comment getComment(String author, String content, int isDeleted){
        return Comment.builder()
                .author(author)
                .content(content)
                .isDeleted(isDeleted)
                .build();
    }
}
