package Project.SangCom.comment.repository;

import Project.SangCom.comment.domain.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    /**
     * 게시글 id & 삭제 여부 조건을 통해 (특정 게시글에 대한) 댓글을 페이징한 결과를 반환
     *
     * 헷갈림.. -> 게시글 마다 댓글 id가 달라지는 게 아니라 어느 게시글이든 댓글은 고유한 id 가짐..
     * findAllByIsDeletedAndPostId 안하고 그냥 삭제됐는지 여부만 체크해도 된다..?
     */
    Slice<Comment> findAllByIsDeletedAndPostId(int isDeleted, Long postId, Pageable pageable);

    /**
     * 특정 게시글의 댓글에 해당하는 댓글들을 모두 불러옴
     * @param postId 댓글을 찾고자하는 게시글의 Id(PK)
     */
    @Query("select c from Comment c where c.post.id = :postId")
    List<Comment> findPostComment(@Param("postId") Long postId);


    @Query("select c from Comment c where c.user.id = :userId")
    List<Comment> findPostContainsUserComment(@Param("userId") Long userId, Pageable pageable);
}
