import React, {Component, useState} from "react";

class ToDo extends Component{
    render() {

        return(
            <div className="TODO">
                <div className="cardcontent">
                </div>
                <button type="button">
                    할 일 추가
                </button>
            </div>
        );
    }
}

export default ToDo;