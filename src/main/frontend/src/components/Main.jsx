import React from 'react';
import BoardPreview from './BoardPreview';
import Class from './Class';
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
            {/** 반 공간 **/}
            <div className="section">
                <p className="section-title">우리 반</p>
                <Class />
            </div>
        </div>
    );
}

