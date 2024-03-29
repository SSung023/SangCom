package Project.SangCom.post.repository;

import Project.SangCom.post.domain.Post;
import Project.SangCom.post.domain.PostCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 게시글 종류 & 삭제 여부 조건을 통해 게시글을 페이징한 결과를 반환
     */
    @Query("select p from Post p where p.isDeleted = :isDeleted AND p.category = :category")
    Slice<Post> findPostNotDeleted
                        (@Param("isDeleted") int isDeleted, @Param("category") PostCategory category, Pageable pageable);

    /**
     * 사용자가 작성한 글들 중 삭제되지 않은 게시글들을 모두 반환
     */
    @Query("select p from Post p where p.user.id = :userId and p.isDeleted = 0")
    Slice<Post> findAllWrotePosts(@Param("userId") Long userId, Pageable pageable);

    /**
     * 게시글 검색 - 제목만 검색
     */
    @Query("select p from Post p where p.title like %:title% AND p.category = :category AND p.isDeleted = 0")
    Slice<Post> searchPostByTitle
                        (@Param("title") String title, @Param("category") PostCategory category, Pageable pageable);

    /**
     * 게시글 검색 - 내용만 검색
     */
    @Query("select p from Post p where p.content like %:content% AND p.category = :category AND p.isDeleted = 0")
    Slice<Post> searchPostByContent
                        (@Param("content") String content, @Param("category") PostCategory category, Pageable pageable);

    /**
     * 게시글 검색 - 제목 + 내용 검색
     */
    @Query("select p from Post p where (p.title like %:title% OR p.content like %:content%) AND p.category = :category AND p.isDeleted = 0")
    Slice<Post> searchPost
                        (@Param("title") String title, @Param("content") String content,
                         @Param("category") PostCategory category, Pageable pageable);

    /**
     * threshold 이내에 작성된 게시글을 대상으로 좋아요 수가 제일 높은 게시글을 하나 선택
     */
    @Query("select p from Post p where p.createdDate > :threshold and p.category = :category and p.isDeleted = 0 order by p.likeCount desc")
    List<Post> findMostLikedPost
                        (@Param("threshold") LocalDateTime threshold, @Param("category") PostCategory category, Pageable pageable);


    @Query("select p from Post p where p.category =:category and p.isDeleted = 0")
    List<Post> findRecentPreviewPosts(@Param("category") PostCategory category, Pageable pageable);
}
