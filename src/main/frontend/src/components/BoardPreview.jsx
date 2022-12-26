import React from 'react';
import { useState } from 'react';
import './BoardPreview.css';
//import Board from '../mock/board.json';
import BoardEmpty from '../mock/boardFake.json';

// board 종류별로 key가 있다고 가정. 전달받은 data(배열 형식)에서 id 별로 보여주는 내용을 달리함
// axios를 사용해 데이터를 받아오면 JSON 형식임. 이걸 가공해서 처리하기 위해 배열로 바꾸는 함수 필요
export default function BoardPreview({ boardKey }) {
    const db = Object.values(BoardEmpty); //JSON 파일을 배열로 변환
    const [isEmpty, setIsEmpty] = useState(db.lenth === 0);

    return (
        <div className='preview'>
            {boardKey === 0 && <div className='title'>자유게시판</div>}
            {boardKey === 1 && <div className='title'>학생회 공지</div>}
        </div>
    );
    !isEmpty && db[boardKey].map((item) => {
        return (
            <div className='board-preview'>
                
            </div>
        );
    });
    return (
        <div className='board-empty'>
            <p>보드가 비어있습니다.</p>
        </div>
    );   
}

