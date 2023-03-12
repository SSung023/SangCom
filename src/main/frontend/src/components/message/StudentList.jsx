import React, { useEffect, useState } from 'react';
import styles from './StudentList.module.css';
import classInfo from '../../mock/attendance.json';
import { MdKeyboardArrowDown } from 'react-icons/md';

export default function StudentList() {
    const [attendance, setAttendance] = useState()
    const testData = classInfo;
    return (
        <div className={styles.studentList}>
            <List classInfo={testData}/>
            <List classInfo={testData}/>
            <List classInfo={testData}/>
            <List classInfo={testData}/>
            <List classInfo={testData}/>
            <List classInfo={testData}/>
            <List classInfo={testData}/>
        </div>
    );
}

function List({ classInfo }) {
    const [toggle, setToggle] = useState(false);
    const [checkAll, setAll] = useState(false);

    const renderList = (students) => {
        return Object.values(students).map((student) => (
            <div key={`${student}`}>
                <input type="checkbox" name="student" id={`${student}`} />
                <label htmlFor={`${student}`}>{`${student}`}</label>
            </div>
        ));
    }

    return (
        <div className={styles.list}>
            <div className={styles.class}>
                <p className={styles.classInfo}>{`${classInfo.grade}학년 ${classInfo.class}반`}</p>
                <MdKeyboardArrowDown onClick={() => setToggle(prev => !prev)}/>
            </div>

            {toggle && 
            <div className={styles.classList}>
                <form className={styles.form}>
                    <div className={styles.selectAll}>
                        <input type="checkbox" name="all" id="all" checked={checkAll} onChange={(e) => setAll(e.target.checked)}/>
                        <label htmlFor="all">전체 선택</label>
                    </div>
                    
                    <div className={styles.students}>
                        {classInfo && renderList(classInfo.students)}
                    </div>

                    <button className={styles.sendBtn}>보내기</button>
                </form>
            </div>}
        </div>
    );
}
