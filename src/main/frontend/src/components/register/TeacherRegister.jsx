import React, {useState} from "react";
import style from './Register.module.css'
import {MdSchool} from "react-icons/md";
import Subjects from '../../mock/subjects.json'

export default function TeacherRegister(){
    const [subjects, setSub] = useState(Object.values(Subjects));
    const email = new URL(window.location.href).searchParams.get("email");

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
                    <label><span>이메일</span>
                        <input
                            type="text"
                            name="email"
                            className={style.inputBox}
                            value={`${email}`}
                            readOnly
                        />
                    </label>
                    <label><span>이름</span>
                        <input
                            type="text"
                            name="name"
                            className={style.inputBox}
                            placeholder="이름 입력"
                            required/>
                    </label>
                    <label><span>담당 학년</span>
                        <select className={style.select} id="teacherGrades" name="teacherGrades" size="1" required>
                            <option value="" hidden selected>담당 학년 선택</option>
                            <option value="1">1</option>
                            <option value="2">2</option>
                            <option value="3">3</option>
                        </select>
                    </label>
                    <label><span>담당 과목</span>
                        <select className={style.select} id="subjects" name="subjects" size="1" required>
                            <option value="" hidden selected>담당 과목 선택</option>
                            {subjects[0].map((subject)=>(
                                <option value={subject.name}>{`${subject.name}`}</option>)) }
                        </select>
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