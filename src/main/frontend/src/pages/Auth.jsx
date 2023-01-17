import React from 'react';
import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { setTokenAction } from '../reducers/jwtReducer';
import axios from 'axios';
import { loginAction } from '../reducers/loginReducer';

// BE로부터 access token을 axios 통신으로 받아와 store에 저장
export default function Auth() {
    // 쿼리에서 email 정보 받아 오기
    const email = new URL(window.location.href).searchParams.get("email");

    // Redux 사용
    const dispatch = useDispatch();
    const actoken = useSelector((state) => state.loginReducer.user.access_token);

    const axios_post = () => {
        const tokenUrl = "/api/auth/token";

        axios.post(tokenUrl, {
            email: email
        })
        .then(function (res) {
            const accessToken = res.headers.get('Authorization');
            // Dispatch에 토큰 전달
            dispatch(setTokenAction(accessToken));
        })
        .catch(function (e) {
            console.log(e);
        })
    };

    
    // TODO access token과 user의 정보를 통신이 모두 끝나고 성공했을 때 저장하는 방식으로 바꾸기
    
    useEffect(axios_post, []);
    useEffect(() => {
        const userUrl = "/api/auth/user";
        const dummyUserInfos = {
            role: "STUDENT",
            username: "김다은",
            nickname: "단두대",
            grade: "1",
            classes: "7",
            number: "2",
            chargeSubject: "",
            chargeGrade: ""
        };

        if(actoken) {
            axios.get(userUrl, {
                headers: {
                    Authorization: `${actoken}`,
                }
            })
            .then(function (res) {
                console.log(res.data);
                dispatch(loginAction(dummyUserInfos));
                // TODO: main page redirect part
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

