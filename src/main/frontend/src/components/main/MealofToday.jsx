import React, {Component,useState} from "react";
import cafeteria from '../../mock/cafeteria.json';
import CardButton from "./CardButton";
import styles from './Dailycard.module.css';
import MealList from "../meal/MealList";
import {useNavigate} from "react-router-dom";

export default function MealofToday(props){
        const today = new Date();
        const year = today.getFullYear();
        const month = today.getMonth();
        const date = today.getDate();
        const monthEnd = new Date(year, month + 1, 0); // 이번달의 말일(예시: 2023년 1월 31일)

        const monthStart = new Date(year, month, 1); //이번달의 시작

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

        return(
                <div className={styles.mealoft}>
                    <div className={styles.todaytitle}>{props.title}</div>
                        <div className={styles.cardcontent}>
                            <div className={styles.cardcontentmeal}>
                                <MealList
                                    active = {true}

                                    /*
                                  현재 2월 : 학교 급식 없음
                                  밑의 코드는 각 달에 1씩 더해 임시로 3월달을 표시함
                                  FROM_YMD : 금월 시작 날짜(8자리 년월일)
                                  TO_YMD : 금월 마지막 날자(8자리 년월일)
                                  */
                                    FROM_YMD = {year.toString() + monthString(monthStart.getMonth()) + dateString(monthStart.getDate())}
                                    TO_YMD = {year.toString() + monthString(monthEnd.getMonth()) + dateString(monthEnd.getDate())}
                                    dateOfToday = {year.toString() + monthString(today.getMonth()) + dateString(today.getDate())}
                                    name = "meal-today"
                                />
                            </div>
                        </div>
                    <CardButton title = {props.button} name = "meal"/>
                </div>
        );

}
