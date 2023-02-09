import React from 'react';
import styles from "./AccessLayout.module.css";

export default function AccessLayout({ children }) {
    return (
        <div className={styles.container}>
            <SangcomLogo />
            <div className={styles.verticalDivider} />
            <div className={styles.options}>
                { children }
            </div>
        </div>
    );
}

function SangcomLogo(){
    return (
        <div className={styles.logoContainer}>
            <div className={styles.serviceName}>
                SangCom
            </div>
            <div className={styles.serviceInfo}>
                상명여자고등학교 커뮤니티
            </div>
        </div>
    );
}