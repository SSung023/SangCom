import React, {Component} from "react";
import CardButton from "./CardButton";
import style from "./Dailycard.module.css";

export default function NextClass(props){
        return(
                <div className={style.nextclass}>
                    <div className={style.todaytitle}>{props.title}</div>
                    <div className={style.classtime}>
                        {/*시간별 교시 구하기*/}
                        3교시
                    </div>
                    <div className={style.cardcontent}>
                        {/*시간표*/}
                        <ul className={style.cardcontentul}>
                            <li>수학</li>
                        </ul>
                    </div>
                    <CardButton title = {props.button}/>
                </div>
        );
}
