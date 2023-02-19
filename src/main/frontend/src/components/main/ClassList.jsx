import React, {useEffect,useState} from "react";
import {defaultInstance} from "../../utility/api";
import styles from "./Dailycard.module.css";

export default function ClassList(props){

    const dates = props.date;
    const grades = props.grades;
    const classes = props.classes;

    const url =
        "https://open.neis.go.kr/hub/hisTimetable" +
        "?KEY=" + process.env.REACT_APP_NEIS_KEY +
        "&Type=json&pIndex=1&pSize=10" +
        "&ATPT_OFCDC_SC_CODE=" + process.env.REACT_APP_NEIS_ATPT_OFCDC_SC_CODE +
        "&SD_SCHUL_CODE=" + process.env.REACT_APP_NEIS_SD_SCHUL_CODE +
        "&ALL_TI_YMD=" + dates +
        "&GRADE="+ grades +
        "&CLASS_NM=" + classes

    const [data, setData] = useState([]);

    const getAllData = () => {
        defaultInstance
            .get(url)
            .then((response) => {
                setData(response.data.hisTimetable[1].row)
            })
            .catch((error) => {
                console.log(error)
            });
    };

    useEffect(() => {
        getAllData();
    }, [getAllData]);


    let lists = [];
    const dateClassArray = () => {
        return data ? (
            data.map((data) => {
                lists.push([data.PERIO,data.ITRT_CNTNT]);
                return(
                    <></>
                )
            })
        ) : (
            <p>No data</p>
        );
    }


        dateClassArray();
        for (let i = 0; i< lists.length; i++){
            if(props.time === lists?.[i]?.[0]) {
                return(
                    <div>
                        <ul className={styles.Listul}
                            key={i}>
                            <li>{lists?.[i]?.[1]}</li>
                        </ul>
                    </div>
                )
            } else {
                lists.push([props.time,"수업 없음"])
            }
        }


}