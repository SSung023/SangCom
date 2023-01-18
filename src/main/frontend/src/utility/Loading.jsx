import React from 'react';
import Spinner from '../../images/spinner.gif';
import styles from './Loading.module.css';

export default function Loading() {
    return (
        <div className={styles.bg}>
            <img src={Spinner} alt="로딩 중" width="5%" />
            <p className={styles.text}>잠시만 기다려 주세요.</p>
        </div>
    );
}

