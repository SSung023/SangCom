import React, {Component} from "react";

class Mainmenu extends Component{
    render() {
        return (
            <div className="mainMenu">
                <ul>
                    <li><a href="/board">게시판</a></li>
                    <li><a href="/studentCouncil">학생회</a></li>
                    <li><a href="/club">동아리</a></li>
                    <li><a href="/timeTable">시간표</a></li>
                </ul>
            </div>
        );
    }
}

export default Mainmenu;