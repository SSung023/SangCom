import React, {Component,useState} from "react";
import cafeteria from '../mock/cafeteria.json';
import CardButton from "./CardButton";

export default function MealofToday(props){
        const [menus, setMenus] = useState(Object.values(cafeteria));
        return(
                <div className="mealoft">
                    <div className="todaytitle">{props.title}</div>
                        <div className="cardcontent">
                            {menus[0].map((menu)=>(
                                <ul className="Listul">
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
