package Project.SangCom.post.repository;

import Project.SangCom.post.domain.Post;
import Project.SangCom.post.domain.PostCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 게시글 종류 & 삭제 여부 조건을 통해 게시글을 페이징한 결과를 반환
     */
    @Query("select p from Post p where p.isDeleted = :isDeleted AND p.category = :category")
    Slice<Post> findPostNotDeleted
                        (@Param("isDeleted") int isDeleted, @Param("category") PostCategory category, Pageable pageable);

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
    @Query("select p from Post p where (p.title like %:title% OR p.content like %:content%) " +
            "AND p.category = :category AND p.isDeleted = 0")
    Slice<Post> searchPost
                        (@Param("title") String title, @Param("content") String content,
                         @Param("category") PostCategory category, Pageable pageable);
}
