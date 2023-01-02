import React from "react";
import styles from "./Leftside.module.css";

export default function Avartar(props){
    return (
        <div className={styles.avartar}>
            <img 
            className={styles.photo} 
            src={props.photoURL} 
            alt=""
            />
            <p className={styles.userName}>
                {`${props.user.name}`}
            </p>
            <p className={styles.userInfo}>
                {`${props.user.grade}학년 ${props.user.class}반 ${props.user.number}번`}
            </p>

            <a href="/my" className={`${styles.buttons} ${styles.highlight}`}>내 정보</a>
            <a href="/logout" className={styles.buttons}>로그아웃</a>

            <div className="horizontalDivider"></div>
        </div>
    );
}