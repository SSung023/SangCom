import React from 'react';
import './Class.css';
import ClassBody from './ClassBody';
import ClassLeftside from "./ClassLeftside";

export default function Class() {
    return (
        <div className='class'>
            <ClassLeftside/>
            <ClassBody />
        </div>
    );
}

