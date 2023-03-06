import React from 'react';
import TopNav from '../components/Topnav';
import { Outlet } from 'react-router-dom';


export default function Root() {

    return (
        <div>
            <TopNav />
            <Outlet />
        </div>
    );
}

