import React, {useState} from "react";
import styles from './ArticleDetail.module.css';
import defaultProfile from '../../images/defualtProfile.svg';
import {MdChatBubbleOutline, MdFavoriteBorder, MdOutlineFavorite, MdBookmarkBorder} from "react-icons/md";


export default function ArticleContent({ articleInfo }){
    const [article, ] = useState(() => articleInfo);

    return(
        <div className={styles.wrapper}>
            <div className={styles.article}>
                <div className={styles.articleInfo}>
                    <img
                        className={styles.photo}
                        src={defaultProfile}
                        alt=""
                    />
                    <BasicInfo
                        createdDate={article.createdDate}
                        author={article.author}/>
                </div>
                <Content
                    className={styles.content}
                    article={article}/>
                <ResponseInfo
                    isLike={article.isLikePressed}
                    likeCnt={article.likeCount}
                    commentCnt={article.commentCount} />
                <div className={styles.articleButtons}>
                    <button>좋아요</button>
                    {article.isOwner ? <button>수정</button> : null}
                    {article.isOwner ? <button>삭제</button> : null}
                </div>
                {!(article.isOwner) && <button className={styles.report}>신고</button>}
            </div>
        </div>
    )
}

function Content({ article }){

    return(
        <div>
            <div className={styles.title}>{article.title}</div>
            <div className={styles.content}>{article.content}</div>
        </div>
    )
}

function BasicInfo({ createdDate, author }){
    const today = new Date();
    const createdTime = new Date(createdDate);

    const rformatter = new Intl.RelativeTimeFormat('ko', {numeric: 'auto'});
    const dformatter = new Intl.DateTimeFormat('ko', {dateStyle: 'short', timeStyle: 'short'});

    const timePassed = Math.ceil((createdTime - today) / (1000 * 60));

    return (
        <div className={styles.info}>
            <p className={styles.userName}>{author}</p>
            {timePassed >= 60
                ? <time>{rformatter.format(timePassed, 'minute')}</time>
                : <time>{dformatter.format(createdTime)}</time>
            }
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