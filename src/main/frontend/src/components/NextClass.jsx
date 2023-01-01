import React, {Component} from "react";
import CardButton from "./CardButton";

export default function NextClass(props){
        return(
                <div className="nextclass">
                    <div className="todaytitle">{props.title}</div>
                    <div className="classtime">
                        {/*시간별 교시 구하기*/}
                        3교시
                    </div>
                    <div className="cardcontent">
                        {/*시간표*/}
                        <ul className="Listul">
                            <li>수학</li>
                        </ul>
                    </div>
                    <CardButton title = {props.button}/>
                </div>
        );
}
