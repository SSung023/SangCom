import React, { useEffect, useState } from 'react';
import { authInstance } from '../../utility/api';
import ChatRoom from './ChatRoom';
import ChatRoomPreview from './ChatRoomPreview';
import styles from './MessageList.module.css';

export default function MessageList() {
    const [ curChatRoomId, setChatRoomId ] = useState();
    const [ messageList, setMessageList ] = useState([
        {
            id: 1,
            isDirect: 0,
            lastMessage: "안녕하세요 2학년 2반 권경란입니다. 방금 올리신 과제물 공지에 대해 질문이 있습니다.",
            userInfo: [
                { userId: 0, displayName: "홍길동" }
            ]
        },
        {
            id: 2,
            isDirect: 0,
            lastMessage: `3월 11일자 수학 과제 공지입니다. 
            \n 1. 교과서 33pg.~40pg 연습문제 풀어오기
            다음 수업 발표자는 준비해오세요.`,
            userInfo: [
                { userId: 0, displayName: "권경란" },
                { userId: 1, displayName: "김다은" },
                { userId: 2, displayName: "이가현" },
                { userId: 3, displayName: "성희연" },
                { userId: 4, displayName: "강승아" },
                { userId: 5, displayName: "홍혜민" },
                { userId: 6, displayName: "빙가현" },
                { userId: 7, displayName: "최고심" },
                { userId: 8, displayName: "백재롱" },
                { userId: 9, displayName: "강백호" },
                { userId: 10, displayName: "문소희" }
            ]
        }
    ]);

    const handleChatRoomId = (id) => {
        setChatRoomId(id);
    }

    const createChatroomPreviews = () => {
        return (messageList.map((chatRoom) => {
            return <ChatRoomPreview chatRoomInfo={chatRoom} setChatRoomId={handleChatRoomId} key={chatRoom.id}/>
        }));
    }

    return (
        <div className={styles.dmContainer}>
            <div className={styles.lists}>
                { messageList ? createChatroomPreviews() : '📭 아직 채팅방이 없어요' }
            </div>
            <div className={styles.chatRoom}>
                { curChatRoomId ? <ChatRoom id={curChatRoomId} /> : `💬 채팅방을 선택하세요` }
            </div>
        </div>
    );
}

