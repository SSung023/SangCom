import React, { useEffect, useState } from "react";
import { defaultInstance } from "../../utility/api";
import styles from "../main/Dailycard.module.css";

export default function MealList(props) {
    // 나이스 급식 주소
    const url =
        process.env.REACT_APP_NEIS_MEAL_URL +
        "?KEY=" +
        process.env.REACT_APP_NEIS_KEY +
        "&Type=json&pIndex=1&pSize=31" +
        "&ATPT_OFCDC_SC_CODE=" +
        process.env.REACT_APP_NEIS_ATPT_OFCDC_SC_CODE +
        "&SD_SCHUL_CODE=" +
        process.env.REACT_APP_NEIS_SD_SCHUL_CODE +
        "&MMEAL_SC_CODE=" +
        process.env.REACT_APP_NEIS_MMEAL_SC_CODE +
        "&MLSV_FROM_YMD=" +
        props.FROM_YMD +
        "&MLSV_TO_YMD=" +
        props.TO_YMD;

    // 해당 달의 메뉴를 data 에
    const [data, setData] = useState([]);

    const getAllData = () => {
        // axios 인스턴스로 정보 가져오기
        defaultInstance
            .get(url)
            .then((response) => {
                setData(response.data.mealServiceDietInfo[1].row);
            })
            .catch((error) => {
                console.log(error);
            });
    };

    useEffect(() => {
        getAllData();
    }, []);

    let lists = [];
    const dateMealArray = () => {
        // data에 저장된 급식 정보에서 날짜와 메뉴만 따로 저장
        return data ? (
            data.map((data) => {
                //MLSV_YMD : 8자리 년월일 , DDISH_NM : 메뉴
                lists.push([data.MLSV_YMD, data.DDISH_NM]); //[[날짜1,메뉴1],[날짜2,메뉴2]]
                return <></>;
            })
        ) : (
            <p>No data</p>
        );
    };

    if (props.active) {
        // active 에 해당하는 날만 급식 메뉴 보여주기
        dateMealArray();

        for (let i = 0; i < lists.length; i++) {
            if (props.dateOfToday === lists?.[i]?.[0]) {
                // 해당 날짜와 일치하는 급식메뉴 return
                lists[i][1] = lists?.[i]?.[1]
                    .replace(/\<br\/\>/g, "\n") //<br/> 태그 삭제
                    .replace(/\*/g, "") // 별표시(*) 삭제
                    .replace(/\(.*(?=\))/g, "") // 알러지정보 삭제
                    .replace(/\)/g, ""); //알러지정보 마지막 괄호 삭제

                lists[i][1] = lists[i][1].split("\n");

                if (props.name === "meal-today") {
                    return (
                        <div>
                            {lists[i][1].map((n) => (
                                <ul className={styles.Listul} key={n}>
                                    <li>{n}</li>
                                </ul>
                            ))}
                        </div>
                    );
                } else if (props.name === "meal-page") {
                    return (
                        <div>
                            <div>
                                {lists[i][1].map((n) => {
                                    return <div key={n}> {n + "\n"} </div>;
                                })}
                            </div>
                        </div>
                    );
                }
            } else {
                lists.push([props.dateOfToday, "급식 없음"]);
            }
        }
    }
}
