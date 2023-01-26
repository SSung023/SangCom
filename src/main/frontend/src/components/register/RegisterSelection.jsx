import React from "react";
import style from './Register.module.css';
import {MdPerson, MdSchool} from "react-icons/md";
import {Link} from "react-router-dom";
import StudentRegister from "./StudentRegister";
export default function RegisterSelection(props){
    const email = new URL(window.location.href).searchParams.get("email");
    const studentUrl = "/studentregister?email=" + email;
    const teacherUrl = "/teacherregister?email=" + email;

    return(
        <div className={style.regSelPage}>
            <div className={style.registerSelection}>
                <img className={style.logo} src={props.SangComLogo}/>
                <div className={style.selection}>
                    <Link to = {studentUrl}>
                        <button
                            type="button"
                            className={style.studentRegister}
                        >
                            <MdPerson className={style.studentIcon}/> 학생으로 가입
                        </button>
                    </Link>
                    <Link to={teacherUrl}>
                        <button type="button" className={style.teacherRegister}>
                            <MdSchool className={style.teacherIcon}/> 교사로 가입
                        </button>
                    </Link>
                </div>

            </div>
        </div>

    );
}