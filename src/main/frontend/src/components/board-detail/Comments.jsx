import React, { useEffect, useState } from 'react';
import { authInstance } from '../../utility/api';
import styles from './Comments.module.css';

export default function Comments({ category, articleId }) {
    const [comments, setComments] = useState();

    useEffect(() => {
        authInstance.get(`/api/board/${category}/${articleId}/comment`)
        .then(function(res) {
            console.log(res.data);
        })
    }, [articleId]);

    return (
        <div className={styles.comments}>
            
        </div>
    );
}

