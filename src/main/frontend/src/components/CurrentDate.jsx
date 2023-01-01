import React, {Component} from "react";
import style from './Dailycard.module.css';

export default function CurrentDate(){
        const getCurrentDate = () => { //오늘 날짜 반환하는 함수
        const currentDate = new Date();
        const dayarr = ['일','월','화','수','목','금','토'];
        const month = currentDate.getMonth()+1;
        const date = currentDate.getDate();
        const day = currentDate.getDay();
        const dot = month + "월 " + date + "일 " + dayarr[day] + "요일 ";
        return dot;
    }
    return(
        <div className={style.date}>
            {`${getCurrentDate()}`}
        </div>
    );
}