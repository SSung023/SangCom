import React from 'react';
import styles from './Dailycard.module.css';
import {useNavigate} from "react-router-dom";

export default function CardButton(props) {
    const navigate = useNavigate();

    const mealAll = () => {
        navigate("/mealPage");
    }

    const courseAll = () => {
        navigate("/timetable");
    }

    if(props.name === "meal"){
        return (
            <div className={styles.btnWrapper}>
                <button type="button" className={styles.btn} onClick={mealAll}>
                    {props.title}
                </button>
            </div>
        );
    } else if (props.name === "course"){
        return (
            <div className={styles.btnWrapper}>
                <button type="button" className={styles.btn} onClick={courseAll}>
                    {props.title}
                </button>
            </div>
        );
    } else if (props.name === "todo"){
        return (
            <div className={styles.btnWrapper}>
                <button type="button" className={styles.btn}>
                    {props.title}
                </button>
            </div>
        );
    }
}

