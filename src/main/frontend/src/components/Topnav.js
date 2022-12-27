import React, {Component} from "react";
import './menu.css'
import Mainmenu from "./Mainmenu";
import Iconmenu from "./Iconmenu";
import schoolLogo from "../images/testLogo.png";

class Topnav extends Component {
    render() {
        return(
            <header>
                <div className="inner">
                    <div className="logo">
                        <a href="/">
                            <img src={schoolLogo} alt="logo of school"/>
                        </a>
                    </div>
                    <div className="navMenus">
                        <div className="flex">
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