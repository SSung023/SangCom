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

    const navigate = useNavigate();

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
    }, []);

    const unauthorized = () => {
        window.location.href = '/' ;
    }

    const render = () => {
        // console.log(isAllowed);
        return (
            isAllowed ? component : unauthorized()
        );
    }

    // category가 undefined면 location으로, 
    // category가 있으면 category로 allowedRole을 검사한다.
    useEffect(() => {
        if(params.category) {
            console.log(`category 검사, category: ${params.category}, allowedList: ${allowedRole[params.category]}, role: ${role[0]}`);
            // setAllowed(allowedRole[params.category].includes(role[0]));
            role && setAllowed(Object.values(role).every((r) => {return allowedRole[params.category].includes(r)}));
        }
        else {
            console.log(`pathname 검사, pathname: ${location.pathname}, allowedList: ${allowedRole[location.pathname]}, role: ${role[0]}`);
            // setAllowed(allowedRole[location.pathname].includes(role[0]));
            role && setAllowed(Object.values(role).every((r) => {return allowedRole[location.pathname].includes(r)}));
        }
    }, [location, role]);

    return (
        isLogin ?
        (render())
        : <Navigate replace to='/login'/>
    );
}