import React, {Component} from "react";
import MealofToday from "./MealofToday";
import ToDo from "./ToDo";
import NextClass from "./NextClass";
import CurrentDate from "./CurrentDate";
import styles from './Dailycard.module.css';

class Dailycard extends Component{
    render() {
        const today = new Date();

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
            <div className={styles.today}>
                {/** title **/}
                <CurrentDate/>

                {/** cards **/}
                <div className={styles.content}>
                    <MealofToday title = "🥕 오늘의 급식" button = "전체 보기"
                                    date = {today.getFullYear().toString()+monthString(today.getMonth())+dateString(today.getDate())}/>
                    <ToDo title = "✔️ 오늘의 할 일" button = "할 일 추가"/>
                    <NextClass title = "✔️ 다음 수업" button = "전체 보기"/>
                </div>
            </div>
        );
    }
}
export default Dailycard;