import React from 'react';
import { useState } from 'react';
import styles from './ArticlePreview.module.css';

export default function ArticlePreview({ articleInfo, customStyle }) {
    const [article, setArticle] = useState(articleInfo);

    return (
        <div className={styles.article} style={customStyle}>
            <p className={styles.articleTitle}>{article.title}</p>
            <p className={styles.articleContent}>{article.content}</p>
            <div className={styles.infos}>
                <BasicInfo createdDate={`02/14 20:23`} author={`익명`} />
                <ResponseInfo likeCnt={`0`} commentCnt={`0`} />
            </div>
        </div>
    );
}

function BasicInfo({ createdDate, author }){
    //const formatter = new Intl.RelativeTimeFormat('ko', {numeric: 'auto'});
    //const today = new Date();
    //const createdTime = new Date(createdDate);
    //const timePassed = Math.ceil((createdTime - today) / 1000 * 60 * 60 * 24);

    return (
        <div className={styles.basicInfo}>
            {/* <p>{formatter.format(timePassed, 'day')}</p> */}
            <time>{createdDate}</time>
            <p>{author}</p>
        </div>
    );
}

function ResponseInfo({ likeCnt, commentCnt }){
    return (
        <div className={styles.responseInfo}>
            <p>{likeCnt}</p>
            <p>{commentCnt}</p>
        </div>
    );
}