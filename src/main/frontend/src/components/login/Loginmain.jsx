import React from "react";
import AccessFrame from "../frames/AccessFrame";
import styles from './login.module.css';
import kakaoIcon from '../../images/kakaoIcon.svg';
import naverIcon from '../../images/naverIcon.svg';
import googleIcon from '../../images/google_gIcon.svg';
import { googleBtnStyle, googleRedirectURL, 
        kakaoBtnStyle, kakaoRedirectURL, 
        naverBtnStyle, naverRedirectURL } from "./SocialLogin";


export default function Loginmain (){
    return(
        <AccessFrame>
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
            <p className={styles.guide}>소셜 계정으로 간편하게 로그인하세요!</p>
        </AccessFrame>
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
                    alt={`${socialName}`}
                />
            </div>
            {socialName}
        </button>
    );
}