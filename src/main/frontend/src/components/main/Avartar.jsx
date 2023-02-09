import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import styles from "./Leftside.module.css";
import defaultProfile from '../../images/defualtProfile.svg';
import { authInstance, defaultInstance } from "../../utility/api";
import { loginAction } from "../../reducers/loginReducer";

export default function Avartar(props){
    const dispatch = useDispatch();
    const userInfo = useSelector((state) => state.loginReducer.user.info);

    const handleLogout = () => {
        defaultInstance.post("/api/auth/logout", {})
        localStorage.setItem("token", "");
    };

    // userInfo 받아 오기
    useEffect(() => {
        authInstance.get("/api/auth/user")
        .then(function(res) {
            dispatch(loginAction(res.data.data));
        })
        .catch(function (error) {
            console.log(error);
        })
    }, []);
        
    // userInfo가 바뀌면 리렌더링 해야 함.
    useEffect(() => {
        //console.log(userInfo);
    }, [userInfo]);

    return (
        <div className={styles.avartar}>
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

            <div>
                <a href="/my" className={`${styles.buttons} ${styles.highlight}`}>내 정보</a>
                <a href="/" className={styles.buttons} onClick={handleLogout}>로그아웃</a>
            </div>
            
            <div className="horizontalDivider"></div>
        </div>
    );
}