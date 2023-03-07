// 로그인이 완료된 사용자만 볼 수 있는 페이지들
// isLogin()이 false면 로그인 페이지를 보여줘야 함

import React, { useEffect, useState } from 'react';
import { Navigate, useLocation, useNavigate, useParams } from "react-router-dom";
import useLogin from './useLogin';
import Unauthorized from '../pages/Unauthorized';
import { useDispatch, useSelector } from 'react-redux';
import { authInstance } from './api';
import { loginAction } from '../reducers/loginReducer';
import { allowedRole } from './allowedRole';

export default function PrivateRoute({ component }) {
    const isLogin = useLogin();
    const location = useLocation();
    const params = useParams();

    const [isAllowed, setAllowed] = useState(true);
    const role = useSelector((state) => state.loginReducer.user.info.role);

    // 사용자 정보 받아오기
    const dispatch = useDispatch();

    useEffect(() => {
        authInstance.get("/api/auth/user")
        .then(function(res) {
            dispatch(loginAction(res.data.data));
        })
        .catch(function (error) {
            console.log(error);
        })
        .then(function () {
            role && allowedRole && setAllowed(Object.values(role).every((r) => {return allowedRole[location.pathname].includes(r)}));
        })
    }, []);

    const unauthorized = () => {
        // console.log(isAllowed);
        window.location.href = '/' ;
    }

    // category가 undefined면 location으로, 
    // category가 있으면 category로 allowedRole을 검사한다.
    useEffect(() => {
        // 게시판의 경우 상세 페이지 때문에 category로 검사해야 함
        if(params.category) {
            // setAllowed(allowedRole[params.category].includes(role[0]));
            role && setAllowed(Object.values(role).every((r) => {return allowedRole[params.category].includes(r)}));
            return;
        }
        role && setAllowed(Object.values(role).every((r) => {return allowedRole[location.pathname].includes(r)}));
    }, [location, role]);

    return (
        isLogin ?
        (isAllowed ? component : unauthorized())
        : <Navigate replace to='/login'/>
    );
}