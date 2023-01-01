import React, {Component, useState} from "react";
import TODOS from '../mock/todo.json';
import CardButton from "./CardButton";
import style from './Dailycard.module.css';

export default function ToDo(props){
        const [todos, setTodos] = useState(Object.values(TODOS));
        return(
            <div className={style.todo}>
                    <div className={style.todaytitle}>{props.title}</div>
                    <div className={style.cardcontenttodo}>
                            {todos[0].map((todo)=>(
                                <ul className={style.cardcontentul}>
                                    <li>{`${todo.title}`}</li>
                                </ul>
                            ))}
                    </div>
                    <CardButton title = {props.button}/>
            </div>

        );
}
