import React from 'react';
import { useState } from 'react';
import styles from './MealPage.module.css';
import Cafeteria from "../main/Cafeteria";
import MealList from "./MealList";


export default function MealPage() {
    const today = new Date();
    return (
        <div className={styles.section}>
            <p className={styles.title}>{today.getMonth()+1}월 급식표</p>
        <table className={styles.meal}>
            <thead className={styles.dates}>
            <CalenderHeader />
            </thead>
            <CalenderBody today={today} />
        </table>
        </div>
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
    const date = today.getDate();

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

    const monthString = (month) => { //해당 날짜의 달을 문자열로 변환
        if (month+1 < 10)
            return "0" + (month+1) //3월은 03 형태로
        else
            return (month+1).toString();
    }

    const dateString = (date) => { //해당 날짜의 일을 문자열로 변환
        if(date < 10)
            return "0" + date; //9일은 09 형태로
        else
            return date.toString();
    }

    while(day < endDate){
        for(let i = 0; i < 7; i++){
            days.push(
                <td className={`${!isSameMonth(day)
                    ? styles.inactive
                    : (isToday(day) ? styles.today : styles.active)}`}
                    key={day}>
                    <div className={styles.date}>
                        {day.getDate()}
                    </div>
                    <div className={styles.todayMeal}>
                        <MealList active = {isSameMonth(day)}

                                  /*
                                  현재 2월 : 학교 급식 없음
                                  밑의 코드는 각 달에 1씩 더해 임시로 3월달을 표시함
                                  FROM_YMD : 금월 시작 날짜(8자리 년월일)
                                  TO_YMD : 금월 마지막 날자(8자리 년월일)
                                  */

                                  FROM_YMD = {year.toString() + monthString(monthStart.getMonth()) + dateString(monthStart.getDate())}
                                  TO_YMD = {year.toString() + monthString(monthEnd.getMonth()) + dateString(monthEnd.getDate())}
                                  dateOfToday = {day.getFullYear().toString() + monthString(day.getMonth()) + dateString(day.getDate())}
                                  name = "meal-page"
                        />
                    </div>
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

