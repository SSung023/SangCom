import React, {Component} from "react";
import { NavLink } from "react-router-dom";
import styles from './Topnav.module.css';

class Mainmenu extends Component{
    render() {
        return (
            <div className={styles.mainMenu}>
                <ul>
                    <li>
                        <NavLink 
                            to='board/free' 
                            style={({isActive}) => ({ color: isActive ? 'var(--blue-color)' : 'var(--txt-color)'})} 
                            className={styles.item}
                        >게시판</NavLink>
                    </li>
                    <li>
                        <NavLink 
                            to='board/council' 
                            style={({isActive}) => ({ color: isActive ? 'var(--blue-color)' : 'var(--txt-color)'})} 
                            className={styles.item}
                        >학생회</NavLink>
                    </li>
                    <li>
                        <NavLink 
                            to='board/club' 
                            style={({isActive}) => ({ color: isActive ? 'var(--blue-color)' : 'var(--txt-color)'})} 
                            className={styles.item}
                        >동아리</NavLink>
                    </li>
                    <li>
                        <NavLink 
                            to='timetable' 
                            style={({isActive}) => ({ color: isActive ? 'var(--blue-color)' : 'var(--txt-color)'})} 
                            className={styles.item}
                        >시간표</NavLink>
                    </li>
                    <li>
                        <NavLink 
                            to='mealPage' 
                            className={styles.item} 
                            style={({isActive}) => ({ color: isActive ? 'var(--blue-color)' : 'var(--txt-color)'})} 
                        >급식표</NavLink>
                    </li>
                </ul>
            </div>
        );
    }
}

export default Mainmenu;