import React from 'react';
import { useState } from 'react';
import './BoardPreview.css';
import Board from '../mock/board.json';
//import BoardEmpty from '../mock/boardFake.json';

function MakeTitle({ boardKey }){
    return boardKey === 0 ? <div className='title'>자유게시판</div> : <div className='title'>학생회 공지</div>
}

// board 종류별로 key가 있다고 가정. 
// 전달받은 data(배열 형식)에서 id 별로 보여주는 내용을 달리함
export default function BoardPreview({ boardKey }) {
    const [db, setDb] = useState(Object.values(Board)); // JSON 파일을 배열로 변환
    const getTime = (time) => {                         // 작성 경과 시간을 구하는 함수
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
        <div className='preview'>
            <MakeTitle boardKey={boardKey} />
            <div className='board'>
                {db[boardKey].map((item) => (
                    <div key={item.id} className='list'>
                        <a href="/board">
                            <p className='board-title'>{item.title}</p>
                            <p className='time'>{`${getTime(item.date)}`}</p>
                        </a>
                    </div>
                ))}
            </div>
        </div>
    );
}