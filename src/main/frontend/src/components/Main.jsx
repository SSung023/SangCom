import React from 'react';
import BoardPreview from './BoardPreview';
import Class from './Class';
import './Main.css';
import Dailycard from "./Dailycard";
import Cafeteria from "./Cafeteria";

export default function Main() {
    return (
        <div className='main'>
            {/** 카드 일정 **/}
            <div className='section'>
                <Dailycard />
            </div>
            {/** 게시판 미리보기 **/}
            <div className='section'>
                <p className='section-title'>게시판 미리보기</p>
                <BoardPreview boardKey={0}/>
                <BoardPreview boardKey={1}/>
            </div>
            {/** 급식표 **/}
            <div className='section'>
                <p className='section-title'>급식표</p>
                <Cafeteria />
            </div>
            {/** 반 공간 **/}
            <div className="section">
                <p className="section-title">우리 반</p>
                <Class />
            </div>
        </div>
    );
}

