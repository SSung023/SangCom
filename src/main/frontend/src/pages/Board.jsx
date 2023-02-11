import React from 'react';
import { useEffect } from 'react';
import ArticleCreate from '../components/board/ArticleCreate';
import { authInstance } from '../utility/api';

export default function Board() {
    useEffect(()=> {
        authInstance.get('api/board/free')
        .then(function (res) {
            console.log(res.data);
        })
    }, []);

    const handleClick = (e) => {
        e.preventDefault();
        authInstance.get('api/board/test');
    }

    return (
        <div className='container'>
            <button onClick={handleClick} >db 생성 버튼</button>
            <ArticleCreate category="FREE" />
        </div>
    );
}

