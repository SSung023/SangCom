import React from 'react';
import styles from './Dailycard.module.css';

export default function CardButton(props) {
    return (
        <button type="button" className={styles.btn}>
            {props.title}
        </button>
    );
}

