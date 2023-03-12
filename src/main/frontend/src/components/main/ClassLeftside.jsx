import React from 'react';
import styles from './ClassLeftside.module.css';
export default function ClassLeftside() {

    return(
        <div className={styles.container}>
            <div className={styles.buttons}>
                <button className={styles.category}>
                    <div className={styles.hashtag}>#</div>
                    <div className={styles.title}>수업</div>
                </button>
                <button className={styles.category}>
                    <div className={styles.hashtag}>#</div>
                    <div className={styles.title}>중간고사</div>
                </button>
                <button className={styles.category}>
                    <div className={styles.hashtag}>#</div>
                    <div className={styles.title}>기말고사</div>
                </button>
                <button className={styles.category}>
                    <div className={styles.hashtag}>#</div>
                    <div className={styles.title}>소통해요</div>
                </button>
            </div>
        </div>
    )
}