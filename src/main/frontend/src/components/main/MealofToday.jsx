import React, {Component,useState} from "react";
import cafeteria from '../../mock/cafeteria.json';
import CardButton from "./CardButton";
import styles from './Dailycard.module.css';

export default function MealofToday(props){
        const [menus, setMenus] = useState(Object.values(cafeteria));
        return(
                <div className={styles.mealoft}>
                    <div className={styles.todaytitle}>{props.title}</div>
                        <div className={styles.cardcontent}>
                            {menus[0].map((menu)=>(
                                <ul className={styles.Listul}>
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
