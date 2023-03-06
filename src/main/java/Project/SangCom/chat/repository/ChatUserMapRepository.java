package Project.SangCom.chat.repository;

import Project.SangCom.chat.domain.ChatRoom;
import Project.SangCom.chat.domain.ChatUserMap;
import Project.SangCom.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatUserMapRepository extends JpaRepository<ChatUserMap, Long> {

    @Query("select c.chatRoom from ChatUserMap c where c.user.id = :userId")
    List<ChatRoom> getMapListByUserId(@Param("userId") Long userId);

    @Query("select c.user from ChatUserMap c where c.chatRoom.id = :roomId")
    List<User> getUserByRoomId(@Param("roomId") Long roomId);
}
