import React, { useEffect } from 'react';
import TopNav from '../components/Topnav';
import { Outlet } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { authInstance } from '../utility/api';
import { loginAction } from '../reducers/loginReducer';


export default function Root() {
    const dispatch = useDispatch();

    useEffect(() => {
        authInstance.get("/api/auth/user")
        .then(function(res) {
            dispatch(loginAction(res.data.data));
            console.log(res);
        })
        .catch(function (error) {
            console.log(error);
        })
    }, []);

    return (
        <div>
            <TopNav />
            <Outlet />
        </div>
    );
}

