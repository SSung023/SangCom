import React from "react";
import style from './Register.module.css';
import {MdPerson, MdSchool} from "react-icons/md";
import {Link} from "react-router-dom";
import AccessFrame from "../frames/AccessFrame";

export default function RegisterSelection(props){
    const email = new URL(window.location.href).searchParams.get("email");
    const studentUrl = "/register/student?email=" + email;
    const teacherUrl = "/register/teacher?email=" + email;

    return(
        <AccessFrame>
            <Link to = {studentUrl}>
                <button
                    type="button"
                    className={style.studentRegister}
                >
                    <MdPerson className={style.icon}/>학생으로 가입
                </button>
            </Link>
            <Link to={teacherUrl}>
                <button type="button" className={style.teacherRegister}>
                    <MdSchool className={style.icon}/>교사로 가입
                </button>
            </Link>
        </AccessFrame>
    );
}