import React from 'react';
import styles from './Leftside.module.css';
import { useState } from 'react';
import Avartar from './Avartar';
import defaultProfile from '../../images/defualtProfile.svg';
import {MdOutlineFormatListBulleted, MdOutlineModeComment, MdOutlineBookmarks} from "react-icons/md";

export default function Leftside() {
    const [user, setUser] = useState({
        name:'홍길동',
        grade:'1',
        class:'12',
        number:'33',
    });

    return (
        <div className={styles.leftSide}>
            <div className={styles.wrapper}>
                {/* 나중에 네트워크 통신으로 user data 받아와서 초기화하는 작업 필요 */}
                <Avartar photoURL={defaultProfile} user={user}/>
                <Menus />
            </div>
        </div>
    );
}


function Menus(){
    return (
        <div className={styles.menus}>
            <div className={styles.horizontalDivider}></div>

            <a href="/myarticle">
                <MdOutlineFormatListBulleted color='2D81FF'/>
                내가 쓴 글
            </a>
            <a href="/mycommentarticle">
                <MdOutlineModeComment color='2D81FF'/>
                댓글 단 글
            </a>
            <a href="/myscrap">
                <MdOutlineBookmarks color='2D81FF'/>
                내 스크랩
            </a>
        </div>
    );
}

