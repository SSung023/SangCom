// 로그인이 완료된 사용자만 볼 수 있는 페이지들
// isLogin()이 false면 로그인 페이지를 보여줘야 함

import React from 'react';
import { Navigate } from "react-router-dom";
import useLogin from './useLogin';

export default function PrivateRoute({ component }) {
    const isLogin = useLogin();
    //const isLogin = false;
    console.log(isLogin);
    return (
        isLogin ? component : <Navigate replace to='/login'/>
    );
}