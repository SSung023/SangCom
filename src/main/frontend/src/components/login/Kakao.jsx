import React from 'react';
import KakaoButton from './KakaoButton';
import { KAKAO_AUTH_URL } from './OAuth';

export default function Kakao() {
    return (
        <div>
            <KakaoButton href={KAKAO_AUTH_URL}>
                <button>카카오 계정 로그인</button>
            </KakaoButton>
        </div>
    );
}

