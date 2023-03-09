import React from 'react';
import Leftside from "../components/main/Leftside";
import TimeTableMain from "../components/main/TimeTableMain";

export default function TimeTable() {
    return (
        <div className='container'>
            <Leftside/>
            <TimeTableMain/>
        </div>
    );
}

