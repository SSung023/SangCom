package Project.SangCom.chat.domain;

import Project.SangCom.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatUserMap {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatUserMap_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatRoom_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 참가한 모든


    //=== 생성 메서드 ===//
    public static ChatUserMap createChatUserMap(User user){
        ChatUserMap chatUserMap = new ChatUserMap();
        chatUserMap.addUser(user);
        return chatUserMap;
    }

    //=== 연관관계 편의 메서드 ===//
    public void addUser(User user){
        this.user = user;
    }

    //=== 비즈니스 코드 ===//
    public void setChatRoom(ChatRoom chatRoom){
        this.chatRoom = chatRoom;
    }
}
