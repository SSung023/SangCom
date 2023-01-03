import React from 'react';
import { useEffect } from 'react';
import Loading from '../components/utility/Loading';

export default function KakaoRedirectHandler() {
    // 인가코드
    let code = new URL(window.location.href).searchParams.get("code");

    useEffect(() => {
        fetch(`/login/oauth2/code/kakao?code=${code}`, {
            method: "GET"
        })
        .then(res => res.json)
        .then(res => {
            console.log(res);
        })
        .catch((e) => {
            console.log(e);
        })
    }, []);

    return (
        <>
            <Loading />
            {console.log(code)}
        </>
    );
}

