import React from 'react';
import Leftside from './Leftside';
import Main from './Main';
//import Dailycard from './Dailycard';

export default function Container() {
    return (
        <div className='container'>
            <Leftside />
            <Main />
        </div>
    );
}

