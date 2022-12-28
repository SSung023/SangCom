import React, {Component, useState} from "react";
import TODOS from '../mock/todo.json';

export default function ToDo(){
        const [todos, setTodos] = useState(Object.values(TODOS));

        return(
            <div className="TODO">
                <div className="cardcontent">
                    {todos[0].map((todo)=>(
                        <ul className="Listul">
                            <li>
                                {`${todo.title}`}
                            </li>
                        </ul>
                    ))}
                </div>
                <button type="button">
                    할 일 추가
                </button>
            </div>
        );
}
