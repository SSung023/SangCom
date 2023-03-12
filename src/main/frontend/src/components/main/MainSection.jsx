import React, { useState } from 'react';
import BoardPreview from './BoardPreview';
import Class from './Class';
import styles from './MainSection.module.css';
import Dailycard from "./Dailycard";

export default function MainSection() {
    const [ categories, ] = useState(['free', 'council']);

    const makeBoardPreviews = () => {
        return categories.map((category) => {
            return <BoardPreview category={category} key={category}/>
        });
    }

    return (
        <div className={styles.main}>
            {/** 카드 일정 **/}
            <div className={styles.section}>
                <Dailycard />
            </div>

            {/** 게시판 미리보기 **/}
            <div className={styles.section}>
                <p className={styles.title}>게시판 미리보기</p>
                <div className={styles.wrapper}>
                    { categories && makeBoardPreviews() }
                </div>
            </div>

            {/** 반 공간 **/}
            <div className={styles.section}>
                <p className={styles.title}>우리 반</p>
                <Class />
            </div>
        </div>
    );
}

