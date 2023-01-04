import React from 'react';
import { useState } from 'react';
import styles from './Meal.module.css';

export default function Meal() {
    const today = new Date();

    return (
        <table className={styles.meal}>
            <thead className={styles.dates}>
                <CalenderHeader />
            </thead>
            <CalenderBody today={today} />
        </table>
    );
}

function CalenderHeader(){
    const [dates, setDates] = useState(['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일']);
    
    return (
        <tr>
            {
                dates.map((date) => (
                    <th key={date}>{date}</th>
                ))
            }
        </tr>
    );
}

function CalenderBody({ today }){
    {/** 
        Date API
        - getDay(): 일요일 시작, 0~6까지 수를 반환
        - getDate(): 현지 시간 기준 일(1~31)을 반환
        - getFullYear(): 네 자리 연도를 반환
        - getMonth(); 
    **/}
    
    const year = today.getFullYear(); //네 자리 연도를 반환
    const month = today.getMonth(); // 0~11까지 반환
    const curDay = today.getDay(); // 0~6까지 반환. 일요일 시작

    const monthStart = new Date(year, month, 1); //이번달의 시작
    const firstDay = monthStart.getDay(); // 이번달의 시작 요일(예시: 2023년 1월 1일은 일요일(0)
    const startDate = new Date(year, month, 1-firstDay); // 달력이 시작하는 날

    const monthEnd = new Date(year, month + 1, 0); // 이번달의 말일(예시: 2023년 1월 31일)
    const lastDay = monthEnd.getDay(); // 이번달의 마지막 요일(예시: 2023년 1월 31은 화요일(2)
    const endDate = new Date(year, month+1, 6-lastDay); //달력이 끝나는 날

    const rows = [];
    let days = [];
    let day = startDate;

    const isSameMonth = (day) => {
        return day.getMonth() === month;
    }

    const isToday = (day) => {
        return day.getDate() === today.getDate();
    }

    while(day < endDate){
        for(let i = 0; i < 7; i++){
            days.push(
                // 
                <td className={`${!isSameMonth(day) 
                                ? styles.inactive 
                                : (isToday(day) ? styles.today : styles.active)}`}
                    key={day}>
                    <div className={styles.date}>
                        {day.getDate()}
                    </div>
                    <div className={styles.todayMeal}></div>
                </td>
            )
            day = new Date(year, day.getMonth(), day.getDate() + 1);
        }
        rows.push(
            <tr className={styles.week} key={day}>
                {days}
            </tr>
        )
        days = [];
    }

    return (
        <tbody>
            {rows}
        </tbody>
    );
}

