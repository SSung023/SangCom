package Project.SangCom.chat.domain;

import Project.SangCom.user.domain.User;
import Project.SangCom.util.formatter.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@ToString(callSuper = true, exclude = {"chatRoom"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseTimeEntity {
    @Id @GeneratedValue
    @Column(name = "chatMessage_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatRoom_id")
    private ChatRoom chatRoom;

    private Long senderId; // 보낸 사람(User)의 PK
    private String senderName; // 보낸 사람의 이름
    private String content; // 메세지 내용


    @Builder
    public ChatMessage(Long senderId, String senderName, String content) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
    }

    // 비즈니스 코드
    public void addChatRoom(ChatRoom chatRoom){
        this.chatRoom = chatRoom;
    }
}
