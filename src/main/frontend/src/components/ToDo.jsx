import React, {Component, useState} from "react";
import TODOS from '../mock/todo.json';

export default function ToDo(){
        const [todos, setTodos] = useState(Object.values(TODOS));

        return(
            <div className="TODO card">
                <div className="cardcontent todo">
                    {todos[0].map((todo)=>(
                        <ul className="Listul">
                            <li>
                                {`${todo.title}`}
                            </li>
                        </ul>
                    ))}
                </div>
            </div>
        );
}
