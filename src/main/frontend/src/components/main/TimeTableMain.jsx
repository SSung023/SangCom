import React from "react";
import styles from "./TimeTableMain.module.css";

export default function TimeTableMain(){
    return(
        <div>
            <Table/>
        </div>
    )
}

const Table = () => {
    return(
        <div className="body">
            <div className={styles.title}>1학기 시간표</div>
            <table className={styles.timeTable}>
                <thead>
                <tr>
                    <th className={styles.period}>교시</th>
                    <th>월요일</th>
                    <th>화요일</th>
                    <th>수요일</th>
                    <th>목요일</th>
                    <th>금요일</th>
                </tr>
                </thead>
                <tbody id="semester">
                <tr>
                    <td className={styles.period}>1교시</td>
                    <td className={styles.subject}>국어</td>
                    <td className={styles.subject}>영어</td>
                    <td className={styles.subject}>수학1</td>
                    <td className={styles.subject}>기술/가정</td>
                    <td className={styles.subject}>역사</td>
                </tr>
                <tr>
                    <td className={styles.period}>2교시</td>
                    <td className={styles.subject}>수학1</td>
                    <td className={styles.subject}>국어</td>
                    <td className={styles.subject}>영어</td>
                    <td className={styles.subject}>과학</td>
                    <td className={styles.subject}>수학2</td>
                </tr>
                <tr>
                    <td className={styles.period}>3교시</td>
                    <td className={styles.subject}>과학</td>
                    <td className={styles.subject}>수학2</td>
                    <td className={styles.subject}>과학</td>
                    <td className={styles.subject}>수학1</td>
                    <td className={styles.subject}>영어</td>
                </tr>
                <tr>
                    <td className={styles.period}>4교시</td>
                    <td className={styles.subject}>영어</td>
                    <td className={styles.subject}>사회</td>
                    <td className={styles.subject}>역사</td>
                    <td className={styles.subject}>사회</td>
                    <td className={styles.subject}>국어</td>
                </tr>
                <tr>
                    <td className={styles.period}>5교시</td>
                    <td className={styles.subject}>음악</td>
                    <td className={styles.subject}>기술/가정</td>
                    <td className={styles.subject}>국어</td>
                    <td className={styles.subject}>미술</td>
                    <td className={styles.subject}>체육</td>
                </tr>
                <tr>
                    <td className={styles.period}>6교시</td>
                    <td className={styles.subject}>수학</td>
                    <td className={styles.subject}>역사</td>
                    <td className={styles.subject}>창체</td>
                    <td className={styles.subject}>미술</td>
                    <td className={styles.subject}>체육</td>
                </tr><tr>
                    <td className={styles.period}>7교시</td>
                    <td className={styles.subject}></td>
                    <td className={styles.subject}></td>
                    <td className={styles.subject}>창체</td>
                    <td className={styles.subject}></td>
                    <td className={styles.subject}></td>
                </tr>
                </tbody>
            </table>

        </div>
    );
}