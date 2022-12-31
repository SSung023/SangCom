import React, {Component} from "react";
import styles from './Menu.module.css';
import {MdAccountCircle, MdCircleNotifications} from "react-icons/md";

class Iconmenu extends Component{
    render() {
        return(
            <div className={styles.iconMenu}>
                <a href="/my">
                    <MdAccountCircle />
                </a>
                <a href="/mynotif">
                    <MdCircleNotifications />
                </a>
            </div>
        )
    }
}
export default Iconmenu;