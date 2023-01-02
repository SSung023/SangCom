import React from 'react';
import { useState } from 'react';
import './Meal.css';

export default function Meal() {
    return (
        <table className='meal'>
            <thead className='dates'>
                <CalenderHeader />
            </thead>
            <Days />
        </table>
    );
}

function MakeDates(){
    const today = new Date();               // 오늘의 날짜

    const year = today.getFullYear();       // 올해 반환
    const month = today.getMonth();         // 0~11까지 반환
    const date = today.getDate();           // 오늘의 Date

    const first = new Date(year, month, 1);  // 이번달의 첫날
    const firstDate = first.getDate();
    const last = new Date(year, month + 1, 0);  // 이번달의 마지막 날

    const dates = [];

    if(firstDate != 0){
        while(firstDate == 0){
            dates.prepend(new Date(year, month, 0-firstDate));
            firstDate--;
        }
    }
    console.log(dates);
    return ({
        dates
    });
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

function Days(){
    {/** Date API의 getDay()는 일요일 시작, 0~6까지 수를 반환 */}
    const dates = ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'];
    
    return (
        <tbody>

            <tr className='days'>
            {
                dates.map((i) => (
                    <td key={i}>{dates[i]}</td>
                ))
            }
            </tr>
        </tbody>
    );
}

