import React from 'react';
import useLogin from "./useLogin";
import { Navigate } from "react-router-dom";

// login이 되어있고 restricted 페이지라면 메인 페이지로 이동
// login이 되어있고 restricted 페이지가 아니라면 정상 이동
export default function PublicRoute({ component, restricted }) {
    const isLogin = useLogin();
    //const isLogin = false;
    return (
        isLogin && restricted ? <Navigate replace to='/'/> : component
    );
}