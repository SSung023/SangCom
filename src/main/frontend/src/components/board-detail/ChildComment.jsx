import React, { useEffect, useState } from 'react';
import styles from './ChildComment.module.css';
import defaultProfile from '../../images/defualtProfile.svg';
import { MdFavoriteBorder, MdOutlineFavorite } from 'react-icons/md';
import { commentDelete, commentLike } from '../../utility/CommentApi';
import { useParams } from 'react-router-dom';


export default function ChildComment({ childCommentInfo, parentId }) {
    const [commentInfo, setComment] = useState(childCommentInfo);
    
    const params = useParams();
    const category = params.category;
    const id = params.id;

    const timestamp = (createdDate) => {
        const dformatter = new Intl.DateTimeFormat('ko', {dateStyle: 'short', timeStyle: 'short'});
        return dformatter.format(createdDate);
    }

    const handlePressLike = async () => {
        // TODO: 부모 댓글까지 같이 보내지면 id로 찾아서 commentInfo 갱신하는 로직 필요
        commentLike(id, commentInfo.id, parentId)
        .then(function(data) {
            setComment(data.data);
        })
    };

    const handlePressDelete = async () => {
        if(window.confirm("댓글을 삭제하시겠습니까?")){
            commentDelete(category, id, commentInfo.id)
            .then(function(data) {
                window.location.replace(window.location.href);
            })
        }
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
                <button onClick={handlePressLike}>좋아요</button>
                <button>신고</button>
                {commentInfo.isOwner && <button onClick={handlePressDelete}>삭제</button>}
            </div>
        </div>
    );
}
