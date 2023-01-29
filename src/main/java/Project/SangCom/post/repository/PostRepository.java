package Project.SangCom.post.repository;

import Project.SangCom.post.domain.Post;
import Project.SangCom.post.domain.PostCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 게시글 종류 & 삭제 여부 조건을 통해 게시글을 페이징한 결과를 반환
     */
    Slice<Post> findAllByIsDeletedAndCategory(int isDeleted, PostCategory category, Pageable pageable);
}
