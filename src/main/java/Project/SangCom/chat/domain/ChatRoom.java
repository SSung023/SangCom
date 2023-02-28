package Project.SangCom.chat.domain;

import Project.SangCom.util.formatter.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatRoom_id")
    private Long id;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<ChatMessage> chatMessages = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<ChatUserMap> chatUserMaps = new ArrayList<>();

    private int isDirect; // 1:1 메세지인지 여부



    //=== 생성 메서드 ===//
    public static ChatRoom createChatRoom(ChatMessage chatMessage, List<ChatUserMap> chatUserMap, int isDirect){
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.addChatMessage(chatMessage);
        for (ChatUserMap userMap : chatUserMap) {
            chatRoom.addChatUserMap(userMap);
        }
        chatRoom.isDirect = isDirect;


        return chatRoom;
    }

    //=== 연관관계 편의 메서드 ===//
    public void addChatMessage(ChatMessage chatMessage){
        this.chatMessages.add(chatMessage);
        chatMessage.addChatRoom(this);
    }
    public void addChatUserMap(ChatUserMap chatUserMap){
        this.chatUserMaps.add(chatUserMap);
        chatUserMap.setChatRoom(this);
    }
}
