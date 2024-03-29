import React, {Component, useState} from "react";
import TODOS from '../../mock/todo.json';
import CardButton from "./CardButton";
import styles from './Dailycard.module.css';

export default function ToDo(props){
        const [todos, setTodos] = useState(Object.values(TODOS));
        return(
                <div className={styles.todo}>
                    <div className={styles.todaytitle}>{props.title}</div>
                        <div className={styles.cardcontenttodo}>
                            <ul className={styles.Listul}>
                                {todos && todos[0].map((todo)=>(
                                    <li key={todo.id}>
                                        <span className={styles.subject}>{`[${todo.subject}]`}</span>
                                        <span>{`${todo.title}`}</span>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    <CardButton title = {props.button} name = "todo"/>
                </div>
        );
}
