import React, {Component} from "react";
import styles from './Menu.module.css'
import Mainmenu from "./Mainmenu";
import Iconmenu from "./Iconmenu";
import schoolLogo from "../images/testLogo.png";

class Topnav extends Component {
    render() {
        return(
            <header>
                <div className={styles.inner}>
                    <div className={styles.logo}>
                        <a href="/">
                            <img src={schoolLogo} alt="logo of school"/>
                        </a>
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