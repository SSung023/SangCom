import React from 'react';
import BoardPreview from './BoardPreview';
import Class from './Class';
import styles from './Main.module.css';
import Dailycard from "./Dailycard";
import Cafeteria from "./Cafeteria";
import Meal from './Meal';

export default function Main() {
    return (
        <div className={styles.main}>
            {/** 카드 일정 **/}
            <div className={styles.section}>
                <Dailycard />
            </div>
            {/** 게시판 미리보기 **/}
            <div className={styles.section}>
                <p className={styles.title}>게시판 미리보기</p>
                <BoardPreview boardKey={0}/>
                <BoardPreview boardKey={1}/>
            </div>
            {/** 급식표 **/}
            <div className={styles.section}>
                <p className={styles.title}>급식표</p>
                <Cafeteria />
            </div>
            {/** 반 공간 **/}
            <div className={styles.section}>
                <p className={styles.title}>우리 반</p>
                <Class />
            </div>
            {/* <div className={styles.section}>
                <p className={styles.title}>급식표 ver2</p>
                <Meal />
            </div> */}
        </div>
    );
}

