import React from "react";
import style from './login.module.css';

export default function Loginmain (props){
    return(
        <form method="post" action="" id="loginForm">
            <div className={style.loginpage}>
                <div className={style.loginmain}>
                    <img
                        className={style.logo}
                        src={props.logoImg}
                        alt=""/>
                    <input
                        type="email"
                        className={style.inputEmail}
                        placeholder="사용자 이메일 입력"
                        name="email"
                        />
                    <input
                        type="password"
                        className={style.inputPw}
                        placeholder="비밀번호 입력"
                        name="password"
                        />
                    <button
                        type="button"
                        className={style.loginbtn}>
                        <span>로그인</span>
                    </button>
                    <button
                        type="button"
                        className={style.kakaoLogin}
                        >
                        <img
                            className={style.kakaoIcon}
                            src={props.kakaoIcon}/>
                        카카오 로그인
                    </button>
                    <a href="./changePw" className={style.changepw}>
                        비밀번호 재설정
                    </a>
                    <p className={style.signuptext}>계정이 없으신가요?{" "}
                    <a href="./singUp" className={style.signup}>
                        회원가입</a>
                    </p>
                </div>
            </div>
        </form>

    );
}