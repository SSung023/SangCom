import React from "react";

export default function Avartar(props){
    return (
        <div className="avartar">
            <img 
            className="photo" 
            src={props.photoURL} 
            alt=""
            />
            <p className="userName">
                {`${props.user.name}`}
            </p>
            <p className="userInfo">
                {`${props.user.grade}학년 ${props.user.class}반 ${props.user.number}번`}
            </p>

            <a href="/my" className="buttons highlight">내 정보</a>
            <a href="/logout" className="buttons">로그아웃</a>

            <div className="horizontalDivider"></div>
        </div>
    );
}