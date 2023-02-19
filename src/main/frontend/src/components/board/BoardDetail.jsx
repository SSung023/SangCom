import React from 'react';
import { useParams } from 'react-router-dom';
import styles from './BoardDetail.module.css';
import BoardTitle from './BoardTitle';

export default function BoardDetail() {
    const params = useParams();
    const category = params.category;
    const id = params.id;
    
    return (
        <div className={styles.wrapper}>
            <BoardTitle category={category} />
            {/* <ArticleDetail /> */}
            {/* <Comments /> */}
        </div>
    );
}

