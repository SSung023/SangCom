import React from 'react';
import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { setTokenAction } from '../reducers/jwtReducer';
import { loginAction } from '../reducers/loginReducer';
import { useNavigate } from 'react-router-dom';
import { authInstance, defaultInstance } from '../utility/api';

// BE로부터 access token을 axios 통신으로 받아와 store에 저장
export default function Auth() {
    const navigate = useNavigate();

    // 쿼리에서 email 정보 받아 오기
    const email = new URL(window.location.href).searchParams.get("email");

    // Redux 사용
    const dispatch = useDispatch();
    const actoken = useSelector((state) => state.jwtReducer.access_token);

    const axios_post = () => {
        const tokenUrl = "/api/auth/token";

        defaultInstance.post(tokenUrl, {
            email: email
        })
        .then(function (res) {
            const accessToken = res.headers.get('Authorization');
            // Dispatch에 토큰 전달
            dispatch(setTokenAction(accessToken));
            localStorage.setItem("token", accessToken);
        })
        .catch(function (e) {
            console.log(e);
        })
    };

    
    // TODO access token과 user의 정보를 통신이 모두 끝나고 성공했을 때 저장하는 방식으로 바꾸기
    
    useEffect(axios_post, []);
    useEffect(() => {
        const userUrl = "/api/auth/user";

        if(actoken) {
            authInstance.get(userUrl)
            .then(function (res) {
                dispatch(loginAction(res.data));
            })
            .then(function (res) {
                // main 페이지로 이동, history 안 남김
                navigate("/", { replace: true });
            })
            .catch(function (e) {
                console.log(e);
            })
        }
    }, [actoken]);

    return (
        <div>
        </div>
    );
}

