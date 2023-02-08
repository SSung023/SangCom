import React from 'react';
import { useEffect } from 'react';
import { authInstance } from '../utility/api';

export default function Board() {
    useEffect(()=> {
        authInstance.get('api/board/free')
        .then(function (res) {
            console.log(res.data);
        })
    }, []);
    return (
        <div className='container'>
            Board Page 📃
        </div>
    );
}

