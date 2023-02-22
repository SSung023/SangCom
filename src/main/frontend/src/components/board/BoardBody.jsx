import React from 'react';

import BoardTitle from './BoardTitle';
import BestArticle from './BestArticle';
import Search from './Search';
import Previews from './Previews';

import { boardTitle } from '../../utility/setBoardTitle.js';

import styles from './BoardBody.module.css';

export default function BoardBody({ category }) {
    const title = boardTitle(category);

    return (
        <div className={styles.wrapper}>
            <BoardTitle title={title} />
            <BestArticle api={`api/board/${category}/best`}/>
            <Search category={category} boardTitle={title}/>
            <Previews category={category}/>
        </div>
    );
}

