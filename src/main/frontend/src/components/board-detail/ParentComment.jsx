import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import CreateComments from './CreateComments';
import ChildComment from './ChildComment';
import styles from './ParentComment.module.css';
import defaultProfile from '../../images/defualtProfile.svg';
import { MdFavoriteBorder, MdOutlineFavorite } from 'react-icons/md';
import { likeAction } from './BoardActions';
import { authInstance } from '../../utility/api';

export default function ParentComment({ parentCommentInfo }) {
    const [commentInfo, setComment] = useState(parentCommentInfo);
    const [childComments, setChildComment] = useState(parentCommentInfo.childComment);
    const [toggle, setToggle] = useState(false);
    const [isLikePressed, setLike] = useState();
    
    const params = useParams();
    const category = params.category;
    const id = params.id;

    const showChildComments = () => {
        return Object.values(childComments).map(comment => (<ChildComment childCommentInfo={comment} key={comment.id}/>));
    }

    const timestamp = (createdDate) => {
        const dformatter = new Intl.DateTimeFormat('ko', {dateStyle: 'short', timeStyle: 'short'});
        return dformatter.format(createdDate);
    }

    const handleToggle = () => {
        setToggle(prev => !prev);
    }

    const handlePressLike = async () => {
        likeAction(isLikePressed, id, commentInfo.id, 0)
        .then(function(res) {
            console.log(res);
        })
        .then(function() {
            // window.location.replace(window.location.href);
        })
        .catch(function(err) {
            console.log(err);
        })
    };

    useEffect(() => {
        console.log(commentInfo);
        setLike(Boolean(commentInfo.isLikePressed));
    }, [commentInfo]);

    return (
        <div className={styles.parent}>
            <div className={styles.profile}>
                <img className={styles.photo} src={defaultProfile} alt=""/>
                <p className={styles.name}>{commentInfo.authorName}</p>
            </div>
            
            <p className={styles.content}>{commentInfo.content}</p>

            <div className={styles.basicInfo}>
                <p className={styles.time}>{timestamp()}</p>
                {isLikePressed ? <MdOutlineFavorite/> : <MdFavoriteBorder/>}
                <p>{commentInfo.likeCount}</p>
            </div>

            <div className={styles.buttons}>
                <button onClick={handlePressLike}>좋아요</button>
                <button onClick={handleToggle}>{toggle ? `접기` : `대댓글`}</button>
                <button>신고</button>
                {commentInfo.isOwner && <button>삭제</button>}
            </div>
            {childComments && showChildComments()}
            {toggle && <CreateComments category={category} parentId={commentInfo.id} articleId={id}/>}
        </div>
    );
}