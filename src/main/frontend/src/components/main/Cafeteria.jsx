import React, {Component,useState} from "react";
import styles from './Cafeteria.module.css';

export default function Cafeteria(){
        return(
            <Body/>
        );
}

/*달력 몸통*/
const Body = () => {
    return(
        <div className="body">
            <table className={styles.cafeteria}>
                <thead>
                <tr>
                    <th>일요일</th>
                    <th>월요일</th>
                    <th>화요일</th>
                    <th>수요일</th>
                    <th>목요일</th>
                    <th>금요일</th>
                    <th>토요일</th>
                </tr>
                </thead>
                <tbody id="month">
                <tr>
                    <td className={styles.day}>
                        <div className={`${styles.datenum} ${styles.grey}`}>27
                            <div className="content"></div>
                        </div>
                    </td>
                    <td className={styles.day}><div className={`${styles.datenum} ${styles.grey}`}>28</div></td>
                    <td className={styles.day}><div className={`${styles.datenum} ${styles.grey}`}>29</div></td>
                    <td className={styles.day}><div className={`${styles.datenum} ${styles.grey}`}>30</div></td>
                    <td className={styles.day}><div className={styles.datenum}>1</div></td>
                    <td className={styles.day}><div className={styles.datenum}>2</div></td>
                    <td className={styles.day}><div className={styles.datenum}>3</div></td>
                </tr>
                <tr>
                    <td className={styles.day}><div className={styles.datenum}>4</div></td>
                    <td className={styles.day}><div className={styles.datenum}>5</div></td>
                    <td className={styles.day}><div className={styles.datenum}>6</div></td>
                    <td className={styles.day}><div className={styles.datenum}>7</div></td>
                    <td className={styles.day}><div className={styles.datenum}>8</div></td>
                    <td className={styles.day}><div className={styles.datenum}>9</div></td>
                    <td className={styles.day}><div className={styles.datenum}>10</div></td>
                </tr>
                <tr>
                    <td className={styles.day}><div className={styles.datenum}>11</div></td>
                    <td className={styles.day}><div className={styles.datenum}>12</div></td>
                    <td className={styles.day}><div className={styles.datenum}>13</div></td>
                    <td className={styles.day}><div className={styles.datenum}>14</div></td>
                    <td className={styles.day}><div className={styles.datenum}>15</div></td>
                    <td className={styles.day}><div className={styles.datenum}>16</div></td>
                    <td className={styles.day}><div className={styles.datenum}>17</div></td>
                </tr>
                <tr>
                    <td className={styles.day}><div className={styles.datenum}>18</div></td>
                    <td className={styles.day}><div className={styles.datenum}>19</div></td>
                    <td className={styles.day}><div className={styles.datenum}>20</div></td>
                    <td className={styles.day}><div className={styles.datenum}>21</div></td>
                    <td className={styles.day}><div className={styles.datenum}>22</div></td>
                    <td className={styles.day}><div className={styles.datenum}>23</div></td>
                    <td className={styles.day}><div className={styles.datenum}>24</div></td>
                </tr>
                <tr>
                    <td className={styles.day}><div className={styles.datenum}>25</div></td>
                    <td className={styles.day}><div className={styles.datenum}>26</div></td>
                    <td className={styles.day}><div className={styles.datenum}>27</div></td>
                    <td className={styles.day}><div className={styles.datenum}>28</div></td>
                    <td className={styles.day}><div className={`${styles.datenum} ${styles.today}`}>29</div></td>
                    <td className={styles.day}><div className={styles.datenum}>30</div></td>
                    <td className={styles.day}><div className={styles.datenum}>31</div></td>
                </tr>
                </tbody>
            </table>

        </div>
    );
}

