import React from 'react';
import Leftside from './Leftside';
import Main from './Main';

export default function Container() {
    return (
        <div className='container'>
            <Leftside />
            <Main />
        </div>
    );
}

