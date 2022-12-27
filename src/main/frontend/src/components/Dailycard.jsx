import React, {Component} from "react";
import MealofToday from "./MealofToday";
import ToDo from "./ToDo";
import NextClass from "./NextClass";
import './Dailycard.css';
import {MdCheck, MdClass, MdRestaurant} from "react-icons/md";

class Dailycard extends Component{
    render() {
        const getCurrentDate = () => { //오늘 날짜 반환하는 함수
            const currentDate = new Date();
            const dayarr = ['일','월','화','수','목','금','토'];
            const month = currentDate.getMonth()+1;
            const date = currentDate.getDate();
            const day = currentDate.getDay();
            const dot = month + "월 " + date + "일 " + dayarr[day] + "요일 ";
            return dot;
        }
        /*
        <div className = "today">
            <div className='date'>오늘날짜</div>
            <div className="content">
                오늘의 급식 컴포넌트
                오늘의 할 일 컴포넌트
                다음 수업 컴포넌트
            </div>
        */
        return(
            <div className="today">
                <div className='date'>{`${getCurrentDate()}`}</div>
                <div className="content">
                    <div className="mealoft">
                        <div className="todaytitle">
                            <MdRestaurant></MdRestaurant>오늘의 급식
                        </div>
                        <MealofToday></MealofToday>
                    </div>
                    <div  className="todo">
                        <div className="todaytitle">
                            <MdCheck></MdCheck>오늘의 할 일
                        </div>
                        <ToDo></ToDo>
                    </div>
                    <div  className="nextclass">
                        <div className="todaytitle">
                            <MdClass></MdClass>다음 수업
                        </div>
                        <NextClass></NextClass>
                    </div>
                </div>

            </div>
        );
    }
}
export default Dailycard;