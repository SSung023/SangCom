import React from "react";
import registerImage from '../images/loginTitle.svg';
import RegisterSelection from "../components/register/RegisterSelection";

export default function Register(){
    const email = new URL(window.location.href).searchParams.get("email");

    return(
        <div>
            {console.log(email)}
            <RegisterSelection SangComLogo = {registerImage}/>
        </div>
    );
}