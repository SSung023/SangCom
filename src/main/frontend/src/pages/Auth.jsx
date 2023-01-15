import React from 'react';
import { useEffect } from 'react';
import { useState } from 'react';
import { shallowEqual, useDispatch, useSelector } from 'react-redux';
import { setTokenAction } from '../reducers/jwtReducer';
import axios from 'axios';

// BE로부터 access token을 axios 통신으로 받아와 store에 저장
// 
// 
export default function Auth() {
    // Redux 사용
    const dispatch = useDispatch();
    const handleToken = (token) => () => {
        dispatch(setTokenAction(token));
    };

    // 내부적으로 token을 관리하기 위한 state
    const [ accessToken, setAccessToken ] = useState("");
    const email = new URL(window.location.href).searchParams.get("email");

    const token = useSelector(state => state.token, shallowEqual);
    
    const axios_post = () => {
        const url = "/api/auth/token";
        const data = {
            email: email
        }

        axios.post(url, data)
        .then(function (res) {
            console.log(res.headers.get('Authorization'));
            // 내부 state에 토큰 업데이트
            //setAccessToken(res.headers.get('Authorization'));
            // Dispatch에 토큰 전달
            //handleToken(accessToken);
        })
        .catch(function (error) {
            console.log(error);
        })
        .then(function (res) {
            console.log(res);
        })
    };

    return (
        <div>
            {
                useEffect(axios_post, [])
            }
            {/* Store에 있는 토큰 출력하기 */}
            {token}
        </div>
    );
}

