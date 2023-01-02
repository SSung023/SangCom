import React, {Component} from "react";
import CardButton from "./CardButton";
import styles from './Dailycard.module.css';

export default function NextClass(props){
        return(
                <div className={styles.nextclass}>
                    <div className={styles.todaytitle}>{props.title}</div>
                    <div className={styles.classtime}>
                        {/*시간별 교시 구하기*/}
                        3교시
                    </div>
                    <div className={styles.cardcontent}>
                        {/*시간표*/}
                        <ul className={styles.Listul}>
                            <li>수학</li>
                        </ul>
                    </div>
                    <CardButton title = {props.button}/>
                </div>
        );
}
