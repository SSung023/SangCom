import React, {Component} from "react";
import './Dailycard.css';

class MealofToday extends Component{
    render() {
        return(
            <div className="mot">
                {/*api로 오늘 급식 메뉴 불러오기*/}
                <div className="cardcontent">

                </div>
                <button type="button">
                    전체보기
                </button>

            </div>
        );
    }
}
export default MealofToday;