import React, {Component} from "react";
import styles from './Topnav.module.css';
import {MdPerson, MdNotifications} from "react-icons/md";

class Iconmenu extends Component{
    render() {
        return(
            <div className={styles.iconMenu}>
                <a href="/my">
                    <MdPerson />
                </a>
                <a href="/mynotif">
                    <MdNotifications />
                </a>
            </div>
        )
    }
}
export default Iconmenu;