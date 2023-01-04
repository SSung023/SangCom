import React from "react";
import registerImage from '../images/loginTitle.svg';
import RegisterSelection from "../components/register/RegisterSelection";

export default function Register(){
    return(
        <div>
            <RegisterSelection SangComLogo = {registerImage}/>
        </div>
    );
}