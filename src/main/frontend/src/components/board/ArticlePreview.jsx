import React from 'react';
import { useState } from 'react';
import styles from './ArticlePreview.module.css';
import { MdChatBubbleOutline, MdFavoriteBorder, MdOutlineFavorite } from 'react-icons/md';
import { authInstance } from '../../utility/api';
import { Link, useNavigate } from 'react-router-dom';

export default function ArticlePreview({ articleInfo, customStyle }) {
    const [article, setArticle] = useState(() => articleInfo);
    const navigate = useNavigate();

    const handleClick = async (e) => {
        // TODO: 백엔드에 boardCategory를 보내달라고 요청. 변경되면 아래 코드로 교체하기.
        // navigate(`board/${article.boardCategory}/${article.id}`);
        navigate(`${article.id}`);
    };

    return (
        <div className={styles.article} style={customStyle} onClick={handleClick}>
            
            <p className={styles.articleTitle}>{article.title}</p>
            <p className={styles.articleContent}>{article.content}</p>
            <div className={styles.infos}>
                <BasicInfo createdDate={article.createdDate} author={article.author} />
                <ResponseInfo isLike={article.isLikePressed} likeCnt={article.likeCount} commentCnt={article.commentCount} />
            </div>
        </div>
    );
}

function BasicInfo({ createdDate, author }){
    const today = new Date();
    const createdTime = new Date(createdDate);
    
    const rformatter = new Intl.RelativeTimeFormat('ko', {numeric: 'auto'});
    const dformatter = new Intl.DateTimeFormat('ko', {dateStyle: 'short', timeStyle: 'short'});
    
    const timePassed = Math.ceil((createdTime - today) / (1000 * 60));

    return (
        <div className={styles.basicInfo}>
            {timePassed >= 60 
                ? <time>{rformatter.format(timePassed, 'minute')}</time> 
                : <time>{dformatter.format(createdTime)}</time>
            }
            <p>{author}</p>
        </div>
    );
}

function ResponseInfo({ isLike, likeCnt, commentCnt }){
    return (
        <div className={styles.responseInfo}>
            {isLike ? <MdOutlineFavorite/> : <MdFavoriteBorder/>}
            <p>{likeCnt}</p>

            <MdChatBubbleOutline/>
            <p>{commentCnt}</p>
        </div>
    );
}