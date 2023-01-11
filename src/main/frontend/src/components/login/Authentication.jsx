import React, { useState } from 'react';
import axios from 'axios';

// jwt 토큰을 백엔드로부터 받고 백엔드에 정보를 요청
export default function Authentication() {
    const [jwt, setJwt] = useState("token");
    axios.post('/api/login', {
        jwt: {jwt}
    })
    .then(function (res) {
        console.log(res);
    })

    return (
        <div>
            
        </div>
    );
}

