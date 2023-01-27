import React from "react";
import styles from './login.module.css';
import kakaoIcon from '../../images/kakaoIcon.svg';
import naverIcon from '../../images/naverIcon.svg';
import googleIcon from '../../images/google_gIcon.svg';
import { googleBtnStyle, googleRedirectURL, 
        kakaoBtnStyle, kakaoRedirectURL, 
        naverBtnStyle, naverRedirectURL } from "./SocialLogin";

export default function Loginmain (){
    return(
        <div className={styles.loginpage}>
            <SangcomLogo />
            <div className={styles.verticalDivider} />
            <div className={styles.loginmain}>
                <SocialLoginBtn 
                    icon={googleIcon} 
                    socialName="구글 로그인" 
                    redirectURL={googleRedirectURL} 
                    style={googleBtnStyle}
                />
                <SocialLoginBtn 
                    icon={kakaoIcon} 
                    socialName="카카오 로그인" 
                    redirectURL={kakaoRedirectURL} 
                    style={kakaoBtnStyle}
                />
                <SocialLoginBtn 
                    icon={naverIcon} 
                    socialName="네이버 로그인" 
                    redirectURL={naverRedirectURL} 
                    style={naverBtnStyle}
                />
                <p>소셜 계정으로 간편하게 로그인하세요!</p>
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

function SocialLoginBtn({ icon, socialName, redirectURL, style }){
    const handleLogin = () => {
        window.location.href = redirectURL;
    }
    return (
        <button
            type="button"
            className={styles.socialBtn}
            onClick={handleLogin}
            style={style}>
            <div className={styles.iconWrapper}>
                <img
                    className={styles.socialIcon}
                    src={icon}
                />
            </div>
            {socialName}
        </button>
    );
}