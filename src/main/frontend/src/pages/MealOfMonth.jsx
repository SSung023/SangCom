import React from 'react';
import MealPage from '../components/meal/MealPage'
import Leftside from "../components/main/Leftside";

export default function MealOfMonth() {
    return (
        <div className='container'>
            <Leftside/>
            <MealPage/>
        </div>
    );
}

