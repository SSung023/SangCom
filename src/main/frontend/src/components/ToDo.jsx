import React, {Component, useState} from "react";
import TODOS from '../mock/todo.json';
import CardButton from "./CardButton";

export default function ToDo(props){
        const [todos, setTodos] = useState(Object.values(TODOS));
        return(
                <div className="todo">
                    <div className="todaytitle">{props.title}</div>
                        <div className="cardcontent todo">
                            {todos[0].map((todo)=>(
                                <ul className="Listul">
                                    <li>{`${todo.title}`}</li>
                                </ul>
                            ))}
                        </div>
                    <CardButton title = {props.button}/>
                </div>
        );
}
