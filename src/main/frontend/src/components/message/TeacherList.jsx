import React from 'react';
import styles from './TeacherList.module.css';
import defaultProfile from '../../images/defualtProfile.svg';
import { IoChatbubblesSharp } from 'react-icons/io5';

export default function TeacherList() {
    return (
        <div className={styles.teacherList}>
            <Card />
            <Card />
            <Card />
            <Card />
            <Card />
            <Card />
        </div>
    );
}


function Card() {
    const onClickBtn = () => {
        console.log("click!");
    }

    return (
        <div className={styles.card}>
            <div className={styles.profile}>
                <img src={defaultProfile} alt="기본 프로필"/>
                <div onClick={onClickBtn}><IoChatbubblesSharp /></div>
            </div>

            <div className={styles.wrapper}>
                <div className={styles.basicInfo}>
                    <span className={styles.name}>홍길동</span>
                    <span className={styles.belong}>사회 / 2학년 2반</span>
                </div>
                <p>9시 이후엔 연락 불가합니다.<br/>상태 메시지 두 줄 테스트입니다.</p>
            </div>
        </div>
    );
}