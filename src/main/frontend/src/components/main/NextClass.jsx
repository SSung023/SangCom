import React, {Component} from "react";
import CardButton from "./CardButton";
import ClassTime from "./ClassTime";
import styles from './Dailycard.module.css';
import ClassList from "./ClassList";

export default function NextClass(props){
        return(
                <div className={styles.nextclass}>
                    <div className={styles.todaytitle}>{props.title}</div>
                    <div className={styles.classtime}>
                        {/*시간별 교시 구하기*/}
                        <ClassTime name="time"/>
                    </div>
                    <div className={styles.cardcontent}>
                        <ClassTime name="course"/>
                    </div>
                    <CardButton title = {props.button} name = "course"/>
                </div>
        );
}
