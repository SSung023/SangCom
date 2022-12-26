import React, {Component} from "react";
import {MdAccountCircle, MdCircleNotifications} from "react-icons/md";

class Iconmenu extends Component{
    render() {
        return(
            <div className="iconMenu">
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