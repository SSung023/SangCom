package Project.SangCom.chat.repository;

import Project.SangCom.chat.domain.ChatRoom;
import Project.SangCom.chat.domain.QChatRoom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRepositoryImpl {
    private final JPAQueryFactory query;

}
