import React from 'react';
import styles from './ChatRoomPreview.module.css';

export default function ChatRoomPreview({ chatRoomInfo, setChatRoomId }) {
    const userInfo = chatRoomInfo.userInfo;
    const lastMessage = chatRoomInfo.lastMessage.replace(/\n/g, " ");
    const chatRoomId = chatRoomInfo.id;

    const onClick = () => {
        setChatRoomId(chatRoomId);
    }

    return (
        <div className={styles.preview} onClick={onClick}>
            {userInfo && userInfo.length === 1 ? 
            <p className={styles.users}>{`${userInfo[0].displayName}`}</p> : 
            <><span className={styles.users}>{`${userInfo[0].displayName}`}</span><span className={styles.people}>{` 외 ${userInfo.length -1}명`}</span></>}
            
            <p className={styles.content}>{lastMessage}</p>
        </div>
    );
}