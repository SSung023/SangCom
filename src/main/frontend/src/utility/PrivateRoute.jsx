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
    const [isAllowed, setAllowed] = useState(true);
    const role = useSelector((state) => state.loginReducer.user.info.role);

    // category가 undefined면 location으로, 
    // category가 있으면 category로 allowedRole을 검사한다.
    useEffect(() => {
        if(params.category) {
            // console.log(`category 검사, category: ${params.category}, allowedList: ${allowedRole[params.category]}, role: ${role[0]}`);
            // setAllowed(allowedRole[params.category].includes(role[0]));
            setAllowed(Object.values(role).every((r) => {allowedRole[params.category].includes(r)}));
        }
        else {
            // console.log(`pathname 검사, pathname: ${location.pathname}, allowedList: ${allowedRole[location.pathname]}, role: ${role[0]}`);
            // setAllowed(allowedRole[location.pathname].includes(role[0]));
            setAllowed(Object.values(role).every((r) => {allowedRole[location.pathname].includes(r)}));
        }
    }, [location, role]);

    const render = () => {
        // console.log(isAllowed);
        return (
            isAllowed ? component : <Unauthorized />
        );
    }

    return (
        isLogin ?
        component
        : <Navigate replace to='/login'/>
        // TODO: 라우팅 고치기: role이 Root에서 초기화되기 때문에 role이 undefined가 되는 순간 PrivateRoute에서는 아무것도 할 수가 없음.
        // role && render()
    );
}