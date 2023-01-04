import React from 'react';
import Loginmain from '../components/login/Loginmain';
import loginImage from '../images/loginTitle.svg';
import kakaoIcon from '../images/kakaoIcon.svg';

export default function Login() {
    return (
        <div>
            <Loginmain
                logoImg = {loginImage}
                kakaoIcon = {kakaoIcon}
            />
        </div>
    );
}
