import React from "react";
import style from './Register.module.css'
import {MdSchool} from "react-icons/md";

export default function StudentRegister(){
    return(
        <div className={style.RegisterPage}>
            <div className={style.Info}>
                <div className={style.teacherTitle}>
                    <MdSchool className={style.infoIcon}/>
                    <span>교사 추가 정보 입력</span>
                </div>
                <form
                    name="student-info"
                    method="post"
                    className={style.Form}>
                    <label><span>이름</span>
                        <input
                            type="text"
                            name="name"
                            className={style.inputBox}
                            placeholder="이름 입력"/>
                    </label>
                    <label><span>담당 학년</span>
                        <input
                            type="number"
                            name="teachergrade"
                            className={style.inputBox}
                            placeholder="담당 학년 입력"/>
                    </label>
                    <label><span>담당 과목</span>
                        <input
                            type="text"
                            name="subject"
                            className={style.inputBox}
                            placeholder="담당 과목 입력"/>
                    </label>
                    <button
                        type="submit"
                        className={style.teacherSubmitBtn}>
                        확인
                    </button>
                </form>
            </div>
        </div>
    );
}