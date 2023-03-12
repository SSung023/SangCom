import React from 'react';
import styles from './Leftside.module.css';
import Avatar from './Avatar';
import {MdOutlineFormatListBulleted, MdOutlineModeComment, MdOutlineBookmarks} from "react-icons/md";
import { defaultInstance } from '../../utility/api';

export default function Leftside() {
    const handleLogout = () => {
        defaultInstance.post("/api/auth/logout", {})
        localStorage.setItem("token", "");
    };

    return (
        <div className={styles.leftSide}>
            <div className={styles.wrapper}>
                {/* 나중에 네트워크 통신으로 user data 받아와서 초기화하는 작업 필요 */}
                <Avatar />
                
                <div className={styles.buttons}>
                    <a href="/my" className={`${styles.button} ${styles.highlight}`}>내 정보</a>
                    <a href="/" className={styles.button} onClick={handleLogout}>로그아웃</a>
                </div>
                
                <div className="horizontalDivider"></div>

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

