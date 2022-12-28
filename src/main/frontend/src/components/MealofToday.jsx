import React, {Component,useState} from "react";
import './Dailycard.css';
import cafeteria from '../mock/cafeteria.json';

export default function MealofToday(){
        const [menus, setMenus] = useState(Object.values(cafeteria));

        return(
            <div className="mot">
                <div className="cardcontent">
                    {menus[0].map((menu)=>(
                        <ul className="Listul">
                            <li>
                                {`${menu.food}`}
                            </li>
                        </ul>
                    ))}
                </div>
                <button type="button">
                    전체보기
                </button>
            </div>
        );

}
