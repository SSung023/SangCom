package Project.SangCom.chat.repository;

import Project.SangCom.chat.domain.ChatUserMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatUserMapRepository extends JpaRepository<ChatUserMap, Long> {

    @Query("select c from ChatUserMap c where c.user.id = :userId")
    List<ChatUserMap> getChatUserMapList(@Param("userId") Long userId);
}
