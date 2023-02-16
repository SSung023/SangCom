package Project.SangCom.scrap.repository;

import Project.SangCom.scrap.domain.Scrap;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    /**
     * "내 스크랩"을 눌렀을 때, 스크랩했던 게시글들을 모두 반환
     *
     * @param userId 현재 사용자의 아이디(PK)
     */
    @Query("select s from Scrap s where s.user.id = :userId")
    Slice<Scrap> findMyScraps (@Param("userId") Long userId, Pageable pageable);


    /**
     * userId와 postId를 통해 DB에서 Scrap을 꺼내서 받아온다.
     * null일 경우를 대비하여 Optional로 반환
     */
    @Query("select s from Scrap s where s.user.id = :userId and s.post.id = :postId")
    Optional<Scrap> findSavedScrap(@Param("userId") Long userId, @Param("postId") Long postId);
}
