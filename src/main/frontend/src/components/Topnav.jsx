import React, {Component} from "react";
import styles from './Topnav.module.css';
import Mainmenu from "./Mainmenu";
import Iconmenu from "./Iconmenu";
import schoolLogo from "../images/sangcom_logo.png";
import { Link } from "react-router-dom";
import ToggleNavbar from "./ToggleNavbar";
import {MdOutlineExpandMore} from "react-icons/md";

class Topnav extends Component {
    render() {
        return(
            <header>
                <div className={styles.inner}>
                    <Link className={styles.logo} to="/">
                        <img src={schoolLogo} alt="logo of school"/>
                        <span className={styles.title}>Sang Com</span>                  
                    </Link>

                    <Mainmenu/>
                    <Iconmenu/>
                </div>
                <ToggleNavbar>
                    <MdOutlineExpandMore className={styles.expand}/>
                </ToggleNavbar>
            </header>

        )
    }
}

export default Topnav;