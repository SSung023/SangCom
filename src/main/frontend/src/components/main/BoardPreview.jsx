import React, { useEffect } from 'react';
import { useState } from 'react';
import styles from './BoardPreview.module.css';
import Board from '../../mock/board.json';
import { boardTitle } from '../../utility/setBoardTitle';
import { authInstance } from '../../utility/api';

// board 종류별로 key가 있다고 가정. 
// 전달받은 data(배열 형식)에서 id 별로 보여주는 내용을 달리함
export default function BoardPreview({ category }) {
    const [db, setDb] = useState(); // JSON 파일을 배열로 변환

    useEffect(() => {
        authInstance.get(`/api/board/preview/${category}`)
        .then(function(res) {
            setDb(res.data.dataList);
        })
        .catch(function(err) {
            console.log(err);
        })
    }, []);

    const makePreviews = () => {
        return Object.values(db).map((article) => {
            return <MakePost article={article} key={article.id} category={category} id={article.id} title={article.title} date={article.createdDate} />
        });
    };

    return (
        db &&
        <div className={styles.preview}>
            <MakeTitle category={category} />
            { makePreviews() }
        </div>
    );
}

function MakeTitle({ category }){
    const title = boardTitle(category);

    return <div className={styles.boardTitle}>{title}</div>
}

function MakePost({ category, id, title, date }){
    const getTime = (time) => {                         // 작성 경과 시간을 구하는 콜백 함수
        let timeTxt = `방금 전`;
        const curTime = new Date();                     
        const diff = (curTime.getTime() - (new Date(time)).getTime()) / 1000; // Date 연산의 단위는 millisecond
        
        const times = [
            { name: '년', milliSeconds: 60 * 60 * 24 * 365 },
            { name: '개월', milliSeconds: 60 * 60 * 24 * 30 },
            { name: '일', milliSeconds: 60 * 60 * 24 },
            { name: '시간', milliSeconds: 60 * 60 },
            { name: '분', milliSeconds: 60 },
        ];
        
        for(const value of times){
            const betweenTime = Math.floor(diff / value.milliSeconds);
        
            if (betweenTime > 0) {
                timeTxt = `${betweenTime}${value.name} 전`;
                return timeTxt;
            }
        }
        return timeTxt;
    }
    
    return (
        date && <div className={styles.board}>
            <div className={styles.list}>
                <a href={`/board/${category}/${id}`} className={styles.post}>
                    <p className={styles.postTitle}>{title}</p>
                    <p className={styles.time}>{`${getTime(date)}`}</p>
                </a>
            </div>
        </div>
    );
}

