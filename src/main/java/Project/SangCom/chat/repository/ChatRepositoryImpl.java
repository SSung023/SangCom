package Project.SangCom.chat.repository;

import Project.SangCom.chat.domain.QChatRoom;
import Project.SangCom.chat.domain.QChatUserMap;
import Project.SangCom.user.domain.QUser;
import Project.SangCom.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ChatRepositoryImpl {
    private final EntityManager entityManager;
    private final JPAQueryFactory query;


    public List<Long> findJoinedChat(List<Long> userIds){
        QChatRoom qChatRoom = QChatRoom.chatRoom;
        QChatUserMap qChatUserMap = QChatUserMap.chatUserMap;
        QUser qUser = QUser.user;

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        List<Long> chatRoomIds = queryFactory.select(qChatRoom.id)
                .from(qChatRoom)
                .innerJoin(qChatRoom.chatUserMaps, qChatUserMap)
                .innerJoin(qChatUserMap.user, qUser)
                .where(qUser.id.in(userIds))
                .groupBy(qChatRoom.id)
                .having(qUser.id.countDistinct().eq(Long.valueOf(userIds.size())))
                .fetch();

        return chatRoomIds;
    }
}
