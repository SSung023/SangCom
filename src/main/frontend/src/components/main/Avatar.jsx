import React from "react";
import { useSelector } from "react-redux";
import styles from "./Leftside.module.css";
import defaultProfile from '../../images/defualtProfile.svg';
import { defaultInstance } from "../../utility/api";

export default function Avatar(){
    const userInfo = useSelector((state) => state.loginReducer.user.info);

    const handleLogout = () => {
        defaultInstance.post("/api/auth/logout", {})
        localStorage.setItem("token", "");
    };

    return (
        <div className={styles.avatar}>
            <img 
            className={styles.photo} 
            src={defaultProfile} 
            alt=""
            />

            <p className={styles.userNickname}>
                {`${userInfo.nickname}`}
            </p>

            <div className={`${styles.userInfo}`}>
                <p className={styles.userName}>
                    {`${userInfo.username}`}
                </p>
                <p className={styles.userBelong}>
                    {`${userInfo.grade}학년 ${userInfo.classes}반 ${userInfo.number}번`}
                </p>
            </div>

            <div className={styles.buttons}>
                <a href="/my" className={`${styles.button} ${styles.highlight}`}>내 정보</a>
                <a href="/" className={styles.button} onClick={handleLogout}>로그아웃</a>
            </div>
            
            <div className="horizontalDivider"></div>
        </div>
    );
}