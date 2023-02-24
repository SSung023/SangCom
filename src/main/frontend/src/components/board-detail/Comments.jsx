import React, { useEffect, useState } from 'react';
import { authInstance } from '../../utility/api';
import ParentComment from './ParentComment';
import styles from './Comments.module.css';

export default function Comments({ category, articleId }) {
    const [comments, setComments] = useState();

    const showComments = () => {
        return Object.values(comments).map(comment => (<ParentComment parentCommentInfo={comment} key={comment.id}/>));
    };

    useEffect(() => {
        authInstance.get(`/api/board/${category}/${articleId}/comment`)
        .then(function(res) {
            setComments(res.data.dataList);
        })
        .catch(function(err) {
            console.log(err);
        });
    }, [articleId]);

    return (
        <div className={styles.comments}>
            {comments && showComments()}
        </div>
    );
}

