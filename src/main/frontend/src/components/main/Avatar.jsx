import React from "react";
import { useSelector } from "react-redux";
import styles from "./Leftside.module.css";
import defaultProfile from '../../images/defualtProfile.svg';

export default function Avatar(){
    const userInfo = useSelector((state) => state.loginReducer.user.info);
    const role = useSelector((state) => state.loginReducer.user.info.role);

    return (
        <div className={styles.avatar}>
            <img 
            className={styles.photo} 
            src={defaultProfile} 
            alt=""
            />

            <p className={styles.userNickname}>
                {role.includes("STUDENT") ? `${userInfo.nickname}` : `${userInfo.username}`}
            </p>

            {role.includes("STUDENT") ?
                <StudentProfile info={userInfo}/> :
                <TeacherProfile info={userInfo} />}
        </div>
    );
}

function StudentProfile({ info }){
    return (
        <div className={styles.userInfo}>
            <p>{`${info.username}`}</p>
            <p>{`${info.grade}학년 ${info.classes}반 ${info.number}번`}</p>
        </div>
    );
}

function TeacherProfile({ info }){
    return (
        <div className={styles.userInfo}>
            {info.chargeGrade && <p>{`${info.chargeGrade}학년`}</p>}
            <p>{`${info.chargeSubject}`}</p>
        </div>
    );
}