import React, { useCallback, useEffect, useRef, useState } from 'react';
import styles from './ChatRoom.module.css';
import { MdSend } from "react-icons/md";

export default function ChatRoom({ id }) {
    const [ newMessage, setMessage ] = useState('');
    const handleSubmit = (e) => {}

    const textAreaRef = useRef();
    const handleChange = useCallback((e) => {
        e.preventDefault();
        const val = e.target.value;

        setMessage(val);
    }, []);

    useEffect(() => {
        if(textAreaRef.current){
            textAreaRef.current.style.height = `auto`;
            textAreaRef.current.style.height = `${textAreaRef.current.scrollHeight}px`;
        }
    }, [newMessage]);

    return (
        <div className={styles.background}>
            <div className={styles.users}>
                <p>홍길동</p>
            </div>
            <ChatRoomContent />
            <form
                onSubmit={handleSubmit} 
                className={styles.form}>
                <textarea 
                    rows={1}
                    className={styles.message} 
                    onChange={handleChange}
                    value={newMessage}
                    ref={textAreaRef}
                />
                <button type="submit" className={styles.send}><MdSend /></button>
            </form>
        </div>
    );
}

// 채팅방
function ChatRoomContent({ id }){
    // 가장 최근 메시지부터 전달 받음
    const [ chatting, setChat ] = useState([
        {
            id: 8,
            content: `3월 11일자 수학 과제 공지입니다. 
            \n 1. 교과서 33pg.~40pg 연습문제 풀어오기
            다음 수업 발표자는 준비해오세요.`,
            author: "홍길동",
            isOwner: 0,
            createDate: "2023-03-11T11:00:04.689Z"
        },
        {
            id: 7,
            content: "안녕하세요 2학년 2반 권경란입니다. 방금 올리신 과제물 공지에 대해 질문이 있습니다.",
            author: "권경란",
            isOwner: 1,
            createDate: "2023-03-11T11:00:04.689Z"
        },
        {
            id: 6,
            content: `3월 11일자 수학 과제 공지입니다. 
            \n 1. 교과서 33pg.~40pg 연습문제 풀어오기
            다음 수업 발표자는 준비해오세요.`,
            author: "홍길동",
            isOwner: 0,
            createDate: "2023-03-11T11:00:04.689Z"
        },
        {
            id: 5,
            content: "안녕하세요 2학년 2반 권경란입니다. 방금 올리신 과제물 공지에 대해 질문이 있습니다.",
            author: "권경란",
            isOwner: 1,
            createDate: "2023-03-11T11:00:04.689Z"
        },
        {
            id: 4,
            content: `3월 11일자 수학 과제 공지입니다. 
            \n 1. 교과서 33pg.~40pg 연습문제 풀어오기
            다음 수업 발표자는 준비해오세요.`,
            author: "홍길동",
            isOwner: 0,
            createDate: "2023-03-11T11:00:04.689Z"
        },
        {
            id: 3,
            content: "안녕하세요 2학년 2반 권경란입니다. 방금 올리신 과제물 공지에 대해 질문이 있습니다.",
            author: "권경란",
            isOwner: 1,
            createDate: "2023-03-11T11:00:04.689Z"
        },
        {
            id: 2,
            content: `3월 11일자 수학 과제 공지입니다. 
            \n 1. 교과서 33pg.~40pg 연습문제 풀어오기
            다음 수업 발표자는 준비해오세요.`,
            author: "홍길동",
            isOwner: 0,
            createDate: "2023-03-11T11:00:04.689Z"
        },
        {
            id: 1,
            content: "안녕하세요 2학년 2반 권경란입니다. 방금 올리신 과제물 공지에 대해 질문이 있습니다.",
            author: "권경란",
            isOwner: 1,
            createDate: "2023-03-11T11:00:04.689Z"
        },
    ]);
    const [ scrollPos, setScrollPos ] = useState();
    const observer = useRef(
        new IntersectionObserver((entries) => {
            entries.forEach((entry) => {
                // 관찰 대상 요소가 화면에 보일 때 구현
                // 백엔드에 요청 보내기
            })
        })
    );
    const bubbleRef = useRef();

    const makeBubbles = () => {
        return (chatting.map((chat) => {
            return <Bubble chat={chat} key={chat.id} />
        }));
    }

    return (
        <div className={styles.content}>
            {makeBubbles()}
        </div>
    );
}

// 대화 버블
function Bubble({ chat }){
    const othersStyle = {
        borderRadius: "0 1.5em 1.5em 1.5em",
        backgroundColor: `var(--btn-bg-color)`,
        float: `left`,
        clear: `both`
    }
    const meStyle = {
        borderRadius: "1.5em 0 1.5em 1.5em",
        backgroundColor: `var(--white-color)`,
        float: `right`,
        clear: `both`
    }

    return (
        <div className={`${styles.bubbleWrapper} ${chat.isOwner && styles.reverse}`}>
            <div style={chat.isOwner ? meStyle : othersStyle} className={styles.bubble}>
                {chat.content.split('\n').map((line) => {
                    return (<p key={line}>{line}<br/></p>);
                })}
            </div>
            <p className={styles.date}>{`2022. 03. 11. 13:52`}</p>
        </div>
        
    );
}