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
                    <a href="/" className="logo">
                        <img src={schoolLogo} alt="logo of school"/>
                    </a>
                    <Mainmenu></Mainmenu>
                    <Iconmenu></Iconmenu>
                </div>
            </header>

        )
    }
}

export default Topnav;