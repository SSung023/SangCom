import React, {Component} from "react";
import { NavLink } from "react-router-dom";
import styles from './Topnav.module.css';

class Mainmenu extends Component{
    activeStyle = {
        color: 'var(--blue-color)',
    };
    render() {
        return (
            <div className={styles.mainMenu}>
                <ul>
                    <li><NavLink to='board' className={styles.item} activeStyle={this.activeStyle}>게시판</NavLink></li>
                    <li><NavLink to='board' className={styles.item}>학생회</NavLink></li>
                    <li><NavLink to='board' className={styles.item}>동아리</NavLink></li>
                    <li><NavLink to='timetable' className={styles.item} activeStyle={this.activeStyle}>시간표</NavLink></li>
                </ul>
            </div>
        );
    }
}

export default Mainmenu;