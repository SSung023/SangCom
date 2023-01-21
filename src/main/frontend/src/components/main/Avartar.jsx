import React, { useEffect } from "react";
import { useSelector } from "react-redux";
import styles from "./Leftside.module.css";
import defaultProfile from '../../images/defualtProfile.svg';
import axios from 'axios';


export default function Avartar(props){
    const userInfo = useSelector((state) => state.loginReducer.user.info);

    const handleLogout = () => {
        localStorage.setItem("token", "");
    };

    // userInfo 받아 오기
    // axios.get("/api/auth/user", {
    //     headers: {
    //         Authorization: `${localStorage.getItem("token")}`,
    //     }
    // })
    // .then(function (res) {
    //     console.log(res);
    // })
        
    // userInfo가 바뀌면 리렌더링 해야 함.
    useEffect(() => {
        console.log(userInfo);
    }, [userInfo]);

    return (
        <div className={styles.avartar}>
            <img 
            className={styles.photo} 
            src={defaultProfile} 
            alt=""
            />
            <p className={styles.userName}>
                {`${userInfo.username}`}
            </p>
            <p className={styles.userInfo}>
                {`${userInfo.grade}학년 ${userInfo.classes}반 ${userInfo.number}번`}
            </p>

            <a href="/my" className={`${styles.buttons} ${styles.highlight}`}>내 정보</a>
            <a href="/" className={styles.buttons} onClick={handleLogout}>로그아웃</a>

            <div className="horizontalDivider"></div>
        </div>
    );
}