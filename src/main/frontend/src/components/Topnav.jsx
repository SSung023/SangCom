import React, {Component} from "react";
import styles from './Topnav.module.css';
import Mainmenu from "./Mainmenu";
import Iconmenu from "./Iconmenu";
import schoolLogo from "../images/testLogo.png";
import { Link } from "react-router-dom";

class Topnav extends Component {
    render() {
        return(
            <header>
                <div className={styles.inner}>
                    <div className={styles.logo}>
                        <Link to="/main">
                            <img src={schoolLogo} alt="logo of school"/>                          
                        </Link>

                    </div>
                    <div className={styles.navMenus}>
                        <div className={styles.flex}>
                            <Mainmenu></Mainmenu>
                            <Iconmenu></Iconmenu>
                        </div>
                    </div>
                </div>
            </header>

        )
    }
}

export default Topnav;