import React from 'react';
import { useParams } from 'react-router-dom';
import styles from './BoardDetail.module.css';

import BoardTitle from '../board/BoardTitle';
import Comments from './Comments';
import { boardTitle } from '../../utility/setBoardTitle.js';
import CreateComments from './CreateComments';

export default function BoardDetail() {
    const params = useParams();
    const category = params.category;
    const id = params.id;
    const title = boardTitle(category);
    
    return (
        <div className={styles.wrapper}>
            <BoardTitle title={title} />
            {/* <ArticleDetail /> */}
            {/* <Comments /> */}
            <Comments category={category} articleId={id}/>
            {/* For parentComments */}
            <CreateComments category={category} articleId={id}/>
        </div>
    );
}

