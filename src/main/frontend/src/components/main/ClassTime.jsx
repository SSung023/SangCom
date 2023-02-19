import React, {useEffect} from 'react';
import ClassList from "./ClassList";
import {authInstance} from "../../utility/api";
import {loginAction} from "../../reducers/loginReducer";
import {useDispatch, useSelector} from "react-redux";

export default function ClassTime(props){

    const dispatch = useDispatch();
    const userInfo = useSelector((state) => state.loginReducer.user.info);

    useEffect(() => {
        authInstance.get("/api/auth/user")
            .then(function(res) {
                dispatch(loginAction(res.data.data));
            })
            .catch(function (error) {
                console.log(error);
            })
    }, []);

    useEffect(() => {
    }, [userInfo]);

    {/**
     Date API
     - getDay(): 일요일 시작, 0~6까지 수를 반환
     - getDate(): 현지 시간 기준 일(1~31)을 반환
     - getFullYear(): 네 자리 연도를 반환
     - getMonth();
     **/}

    const today = new Date();
    const hours = today.getHours();
    const minutes = today.getMinutes();
    const seconds = today.getSeconds();

    const year = today.getFullYear();
    const month = today.getMonth();
    const date = today.getDate();

    const monthString = (month) => { //해당 날짜의 달을 문자열로 변환
        if (month+1 < 10)
            return "0" + (month+1) //3월은 03 형태로
        else
            return (month+1).toString();
    }

    const dateString = (date) => { //해당 날짜의 일을 문자열로 변환
        if(date < 10)
            return "0" + date; //9일은 09 형태로
        else
            return date.toString();
    }

    /*
        예시로 데이터를 보여주기 위해 작년 시간표를 반영
    */
    const dates = (year-1).toString() + monthString(month+1) + dateString(date);

    /*
        실제 사용 시 밑의 코드 반영
    */
/*
    const dates = year.toString() + monthString(month) + dateString(date);
*/

    const course = [0,1,2,3,4,5,6,7];

    const next=()=>{
        if (hours >= 16){ // 1~7교시가 아닌 시간대는 모두 0교시로 처리
            return course[0]
        }
        else if (hours < 9 && hours >= 0){ // 00시 되자마자 1교시 표시
            return course[1]
        }else if(hours < 10){
            return course[2]
        }else if(hours < 11){
            return course[3]
        }else if(hours < 12){
            return course[4]
        }else if(hours < 14){
            return course[5]
        }else if(hours < 15){
            return course[6]
        }else if(hours < 16){
            return course[7]
        }
    }

    if(props.name === "time"){
        return(
            <div>
                {next()+"교시"}
            </div>
        )
    }
    else if(props.name === "course"){
        return(
            <div>
                <ClassList date={dates}
                           grades={userInfo.grade}
                           classes={userInfo.classes}
                           time={next()}
                />
            </div>
        )
    }

}