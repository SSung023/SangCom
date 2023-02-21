import React, {useState} from "react";
import style from './Register.module.css'
import {MdSchool} from "react-icons/md";
import Subjects from '../../mock/subjects.json'
import {useNavigate} from "react-router-dom";
import {defaultInstance} from "../../utility/api";

export default function TeacherRegister(){
    const navigate = useNavigate();
    const [subjects, setSub] = useState(Object.values(Subjects));

    const email = new URL(window.location.href).searchParams.get("email");

    const [formData, setFormData] = useState({
        role : "teacher",
        email : email,
        username : "",
        nickname : "",
        grade: "",
        classes: "",
        number: "",
        chargeGrade: "",
        chargeSubject: ""
    });

    const handleSubmit = (event) => {
        event.preventDefault();
        defaultInstance.post("api/auth/register",{
            ...formData
        }).then(function (response){
            if(response.status === 200) {
                console.log('register success')
                return navigate(`/`); //로그인 페이지로 이동
            }else{ // 실패
                console.log('register fail')
            }
        }).catch(function(error){ //axios error
            console.log(error);
        });
    }

    const handleChange = (event) => {
        setFormData({
            ...formData,
            [event.target.name]: event.target.value
        });
    }

    return(
        <div className={style.RegisterPage}>
            <div className={style.Info}>
                <div className={style.teacherTitle}>
                    <MdSchool className={style.infoIcon}/>
                    <span>교사 추가 정보 입력</span>
                </div>
                <form
                    name="teacher-info"
                    onSubmit={handleSubmit}
                    className={style.Form}>
                    <label><span>이메일</span>
                        <input
                            type="text"
                            value={`${email}`}
                            className={style.inputBox}
                            readOnly/>
                    </label>
                    <label><span>이름</span>
                        <input
                            type="text"
                            name="username"
                            className={style.inputBox}
                            placeholder="이름 입력"
                            onChange={handleChange}
                            required/>
                    </label>
                    <label><span>담당 학년</span>
                        <select
                            onChange={handleChange}
                            className={style.select} id="chargeGrade" name="chargeGrade" size="1" required>
                            <option value="" hidden>담당 학년 선택</option>
                            <option value="1">1</option>
                            <option value="2">2</option>
                            <option value="3">3</option>
                        </select>
                    </label>
                    <label><span>담당 과목</span>
                        <select
                            onChange={handleChange}
                            className={style.select} id="chargeSubject" name="chargeSubject" size="1" required>
                            <option value="" hidden selected>담당 과목 선택</option>
                            {subjects[0].map((subject)=>(
                                <option value={subject.name} key={subject.name}>{`${subject.name}`}</option>)) 
                            }
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