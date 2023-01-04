import React from "react";
import style from './Register.module.css'
import {MdPerson} from "react-icons/md";

export default function StudentRegister(){
    return(
        <div className={style.RegisterPage}>
            <div className={style.Info}>
                <div className={style.studentTitle}>
                    <MdPerson className={style.infoIcon}/>
                    <span>학생 추가 정보 입력</span>
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
                    <label><span>닉네임</span>
                        <input
                            type="text"
                            name="nickname"
                            className={style.inputBox}
                            placeholder="활동 닉네임 입력"/>
                    </label>
                    <label><span>학년</span>
                        <input
                            type="number"
                            name="studentgrade"
                            className={style.inputBox}
                            placeholder="학년 입력"/>
                    </label>
                    <label><span>반</span>
                        <input
                            type="number"
                            name="studentclass"
                            className={style.inputBox}
                            placeholder="반 입력"/>
                    </label>
                    <label><span>번호</span>
                        <input
                            type="number"
                            name="studentnumber"
                            className={style.inputBox}
                            placeholder="번호 입력"/>
                    </label>
                    <button
                        type="submit"
                        className={style.studentSubmitBtn}>
                        확인
                    </button>
                </form>
            </div>
        </div>
    );
}