import React, { useState } from 'react';
import styles from './ChildComment.module.css';
import defaultProfile from '../../images/defualtProfile.svg';
import { MdFavoriteBorder, MdOutlineFavorite } from 'react-icons/md';


export default function ChildComment({ childCommentInfo }) {
    const [commentInfo, setComment] = useState(childCommentInfo);
    
    const timestamp = (createdDate) => {
        const dformatter = new Intl.DateTimeFormat('ko', {dateStyle: 'short', timeStyle: 'short'});
        return dformatter.format(createdDate);
    }

    return (
        <div className={styles.child}>
            <div className={styles.profile}>
                <img className={styles.photo} src={defaultProfile} alt=""/>
                <p className={styles.name}>{commentInfo.authorName}</p>
            </div>
            
            <p className={styles.content}>{commentInfo.content}</p>

            <div className={styles.basicInfo}>
                <p className={styles.time}>{timestamp()}</p>
                {commentInfo.isLikePressed ? <MdOutlineFavorite/> : <MdFavoriteBorder/>}
                <p>{commentInfo.likeCount}</p>
            </div>

            <div className={styles.buttons}>
                <button>좋아요</button>
                <button>신고</button>
                {commentInfo.isOwner && <button>삭제</button>}
            </div>
        </div>
    );
}
