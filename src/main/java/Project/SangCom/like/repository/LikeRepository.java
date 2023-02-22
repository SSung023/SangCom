package Project.SangCom.like.repository;

import Project.SangCom.like.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {

    @Query("select l from Likes l where l.user.id = :userId AND l.post.id = :postId")
    Optional<Likes> findLikes(@Param("userId") Long userId, @Param("postId") Long postId);

    @Query("select l from Likes l where l.user.id = :userId AND l.comment.id = :commentId")
    Optional<Likes> findCommentLikes(@Param("userId") Long userId, @Param("commentId") Long commentId);
}
