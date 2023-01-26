import React, {useState} from "react";
import style from './Register.module.css'
import {MdPerson} from "react-icons/md";
import {useNavigate} from "react-router-dom";
import {defaultInstance} from "../../utility/api";

export default function StudentRegister(){
    const navigate = useNavigate();
    const email = new URL(window.location.href).searchParams.get("email");

    const [formData, setFormData] = useState({
        role : "student",
        email : email,
        name : "",
        nickname : "",
        grade: "",
        class: "",
        number: "",
        chargeGrade: "",
        chargeSubject: ""
    });

    const handleSubmit = (event) => {
        event.preventDefault();
        defaultInstance.post("api/auth/register",{
            formData
        }).then(function (response){
            if(response.status === 200) {
                console.log(formData)
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

    /***********************************/
    let numbers = [];
    for (let i = 1; i < 41; i++) {
        numbers.push(i)
    }

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
                            name="name"
                            className={style.inputBox}
                            placeholder="이름 입력"
                            onChange={handleChange}
                            required/>
                    </label>
                    <label><span>닉네임</span>
                        <input
                            type="text"
                            name="nickname"
                            className={style.inputBox}
                            placeholder="활동 닉네임 입력"
                            onChange={handleChange}
                            required/>
                    </label>
                    <label><span>학년</span>
                        <select
                            onChange={handleChange}
                            className={style.select} id="grade" name="grade" size="1" required>
                            <option value="" hidden>학년 선택</option>
                            <option value="1">1</option>
                            <option value="2">2</option>
                            <option value="3">3</option>
                        </select>
                    </label>
                    <label><span>반</span>
                        <select
                            onChange={handleChange}
                            className={style.select} id="class" name="class" size="1" required>
                            <option value="" hidden>반 선택</option>
                            <option value="1">1</option>
                            <option value="2">2</option>
                            <option value="3">3</option>
                            <option value="4">4</option>
                            <option value="5">5</option>
                            <option value="6">6</option>
                            <option value="7">7</option>
                        </select>
                    </label>
                    <label><span>번호</span>
                        <select
                            onChange={handleChange}
                            className={style.select} id="number" name="number" size="1" required>
                            <option value="" hidden>번호 선택</option>
                            {numbers.map(function(i){
                                return <option value={numbers[i-1]}>{`${numbers[i-1]}`}</option>
                            })
                            }
                        </select>
                    </label>
                    <button
                        onClick={handleSubmit}
                        type="submit"
                        className={style.studentSubmitBtn}>
                        확인
                    </button>
                </form>
            </div>
        </div>
    );
}