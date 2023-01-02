import React from 'react';
import { useState } from 'react';
import styles from './BoardPreview.module.css';
import Board from '../../mock/board.json';
//import BoardEmpty from '../mock/boardFake.json';

function MakeTitle({ boardKey }){
    return boardKey === 0 ? <div className={styles.boardTitle}>자유게시판</div> : <div className={styles.boardTitle}>학생회 공지</div>
}

function MakePost( props ){
    const [db, setDb] = useState(props.data);
    const getTime = (time) => {                         // 작성 경과 시간을 구하는 콜백 함수
        let timeTxt = `방금 전`;
        const curTime = new Date();                     
        const diff = (curTime.getTime() - (new Date(time)).getTime()) / 1000; // Date 연산의 단위는 millisecond
        
        const times = [
            { name: '년', milliSeconds: 60 * 60 * 24 * 365 },
            { name: '개월', milliSeconds: 60 * 60 * 24 * 30 },
            { name: '일', milliSeconds: 60 * 60 * 24 },
            { name: '시간', milliSeconds: 60 * 60 },
            { name: '분', milliSeconds: 60 },
        ];
        
        for(const value of times){
            const betweenTime = Math.floor(diff / value.milliSeconds);
        
            if (betweenTime > 0) {
                timeTxt = `${betweenTime}${value.name} 전`;
                return timeTxt;
            }
        }
        return timeTxt;
    }

    return (
        <div className={styles.board}>
            {db[props.boardKey].map((item) => (
                // TODO: PostPreview 컴포넌트 만들어보기. css 파일을 별도 생성해 title 클래스 사용 가능하도록.
                // TODO: 게시글 ID가 정해지면 해당 ID로 이동하도록 링크 수정
                <div key={item.id} className={styles.list}>
                    <a href="/board" className={styles.post}>
                        <p className={styles.postTitle}>{item.title}</p>
                        <p className={styles.time}>{`${getTime(item.date)}`}</p>
                    </a>
                </div>
            ))}
        </div>
    );
}

// board 종류별로 key가 있다고 가정. 
// 전달받은 data(배열 형식)에서 id 별로 보여주는 내용을 달리함
export default function BoardPreview({ boardKey }) {
    const [db, setDb] = useState(Object.values(Board)); // JSON 파일을 배열로 변환
    
    return (
        <div className={styles.preview}>
            <MakeTitle boardKey={boardKey} />
            <MakePost boardKey={boardKey} data={db} />
        </div>
    );
}

