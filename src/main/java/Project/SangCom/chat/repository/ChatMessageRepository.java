package Project.SangCom.chat.repository;

import Project.SangCom.chat.domain.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("select m from ChatMessage m where m.chatRoom.id =:roomId order by m.id desc")
    Slice<ChatMessage> getChatMessageList(@Param("roomId") Long roomId, Pageable pageable);
}
