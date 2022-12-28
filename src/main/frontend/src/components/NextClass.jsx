import React, {Component} from "react";

class NextClass extends Component{
    render() {
        return(
            <div className="NEXTCLASS">
                <div className="classtime">
                    {/*시간별 교시 구하기*/}
                    3교시
                </div>
                <div className="cardcontent">
                    {/*시간표*/}
                    <ul className="Listul">
                        <li>수학</li>
                    </ul>
                </div>
                <button type="button">
                    전체보기
                </button>
            </div>

        );
    }
}

export default NextClass;