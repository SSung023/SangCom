import React, { useEffect } from "react";
import { useSelector } from "react-redux";
import styles from "./Leftside.module.css";
import defaultProfile from '../../images/defualtProfile.svg';


export default function Avartar(props){
    const userInfo = useSelector((state) => state.loginReducer.user.info);

    const handleLogout = () => {
        localStorage.setItem("token", "");
    };

    // TODO: userInfo 통신은 Main.jsx에서, Avartar에서는 store에 저장된 state를 가져오는 방식으로 변경
    // axios.get("/api/auth/user", {
    //     headers: {
    //         Authorization: `${localStorage.getItem("token")}`,
    //     }
    // })
    // .then(function (res) {
    //     console.log(res);
    // })
        
    // userInfo가 바뀌면 리렌더링 해야 함.
    // useEffect(() => {
    //     console.log(userInfo);
    // }, [userInfo]);

    return (
        <div className={styles.avartar}>
            <img 
            className={styles.photo} 
            src={defaultProfile} 
            alt=""
            />
            <div className={styles.flexCenter}>
                <p className={styles.userNickname}>
                    {`${userInfo.username}`}
                </p>
                <div className={styles.userInfo}>
                    <p className={styles.userName}>
                        {`${userInfo.name}`}
                    </p>
                    <p className={styles.userBelong}>
                        {`${userInfo.grade}학년 ${userInfo.classes}반 ${userInfo.number}번`}
                    </p>
                </div>
            </div>
            
            <a href="/my" className={`${styles.buttons} ${styles.highlight}`}>내 정보</a>
            <a href="/" className={styles.buttons} onClick={handleLogout}>로그아웃</a>

            <div className="horizontalDivider"></div>
        </div>
    );
}