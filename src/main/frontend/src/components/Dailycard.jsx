import React, {Component} from "react";
import MealofToday from "./MealofToday";
import ToDo from "./ToDo";
import NextClass from "./NextClass";
import CurrentDate from "./CurrentDate";
import styles from './Dailycard.module.css';

class Dailycard extends Component{
    render() {
        return(
            <div className={styles.today}>
                {/** title **/}
                <CurrentDate></CurrentDate>

                {/** cards **/}
                <div className={styles.content}>
                    <MealofToday title = "🥕 오늘의 급식" button = "전체 보기"/>
                    <ToDo title = "✔️ 오늘의 할 일" button = "할 일 추가"/>
                    <NextClass title = "✔️ 다음 수업" button = "전체 보기"/>
                </div>
            </div>
        );
    }
}
export default Dailycard;