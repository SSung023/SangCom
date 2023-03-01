// 로그인이 완료된 사용자만 볼 수 있는 페이지들
// isLogin()이 false면 로그인 페이지를 보여줘야 함

import React, { useEffect, useState } from 'react';
import { Navigate, useLocation, useParams } from "react-router-dom";
import useLogin from './useLogin';
import Unauthorized from '../pages/Unauthorized';
import { useSelector } from 'react-redux';

export default function PrivateRoute({ component, allowedRole }) {
    const isLogin = useLogin();
    const location = useLocation();
    const params = useParams();
    const [isAllowed, setAllowed] = useState();
    const role = useSelector((state) => state.loginReducer.user.info.role);

    // category가 undefined면 location으로, 
    // category가 있으면 category로 allowedRole을 검사한다.
    useEffect(() => {
        if(params.category) {
            setAllowed(allowedRole[params.category].includes(role));
        }
        else {
            setAllowed(allowedRole[location.pathname].includes(role));
        }
    }, [location]);

    console.log("logout please " + !isLogin);

    return (
        isLogin ? 
        component
        : <Navigate replace to='/login'/>
        // (isAllowed ? component : <Unauthorized />) 
    );
}