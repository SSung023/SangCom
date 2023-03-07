import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import styles from './Message.module.css';
import StudentList from './StudentList';
import TeacherList from './TeacherList';

export default function Message() {
    const [toggleSwitch, setToggle] = useState(true);
    const role = useSelector((state) => state.loginReducer.user.info.role);
    const [listBody, setListBody] = useState({
        display: 'block'
    });
    const [dmBody, setDmBody] = useState({
        display: 'none'
    });

    useEffect(() => {
        if(toggleSwitch) {
            setListBody((prev) => ({...prev, display: 'block'}));
            setDmBody((prev) => ({...prev, display: 'none'}));
        }
        else {
            setListBody((prev) => ({...prev, display: 'none'}));
            setDmBody((prev) => ({...prev, display: 'block'}));
        }
    }, [toggleSwitch]);

    return (
        <div className={styles.messagePage}>
            {/* 
                <Switch />
                <TeacherList />
                    - <Teacher />
                    - <Teacher />
                <StudentList />
                    - <Student />
                    - <Student />
                <MessageList />
                <MessageBody />
            */}
            <div className={styles.toggle}>
                <div className={ toggleSwitch ? `${styles.active}` : "" } onClick={() => setToggle(true)}>List</div>
                <div className={ !toggleSwitch ? `${styles.active}` : "" } onClick={() => setToggle(false)}>Message</div>
            </div>

            <div className={styles.body} style={listBody}>
                {role === "TEACHER" ? <StudentList /> : <TeacherList /> }
                {/* <StudentList /> */}
            </div>
            <div className={styles.body} style={dmBody}>

            </div>
        </div>
    );
}