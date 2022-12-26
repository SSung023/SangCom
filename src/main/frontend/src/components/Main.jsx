import React from 'react';
import BoardPreview from './BoardPreview';
import './Main.css';

export default function Main() {
    return (
        <div className='main'>
            {/** 게시판 미리보기 **/}
            <div className='section'>
                <p className='section-title'>게시판 미리보기</p>
                <BoardPreview boardKey={0}/>
                <BoardPreview boardKey={1}/>
            </div>
        </div>
    );
}

