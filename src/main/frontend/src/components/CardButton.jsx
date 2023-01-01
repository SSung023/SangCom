import React from 'react';
import style from './Dailycard.module.css';

export default function CardButton(props) {
    return (
            <button type="button" className={style.cardbtn}>
                {props.title}
            </button>
    );
}

