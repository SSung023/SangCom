import React, {Component,useState} from "react";
import cafeteria from '../mock/cafeteria.json';
import CardButton from "./CardButton";
import style from "./Dailycard.module.css"

export default function MealofToday(props){
        const [menus, setMenus] = useState(Object.values(cafeteria));
        return(
            <div className={style.mealoft}>
                <div className={style.todaytitle}>{props.title}</div>
                <div className={style.cardcontent}>
                    {menus[0].map((menu)=>(
                        <ul className={style.cardcontentul}>
                            <li>
                                {`${menu.food}`}
                            </li>
                        </ul>
                    ))}
                </div>
                    <CardButton title = {props.button}/>
            </div>
        );

}
