import React, { useState } from 'react';
import { useEffect } from 'react';
import { authInstance } from '../../utility/api';
import ArticlePreview from './ArticlePreview';
import styles from './BestArticle.module.css';

export default function BestArticle({ api }) {
    const [article, setArticle] = useState();
    const customStyle = {
        backgroundColor: `var(--btn-bg-color)`,
        borderRadius: `10px`
    };

    useEffect(() => {
        authInstance.get(api)
        .then(function(res) {
            setArticle(res.data.data);
        })
    }, []);

    return (
        <div className={styles.wrapper}>
            <div className={styles.title}>실시간 인기글</div>

            { article && <ArticlePreview articleInfo={article} customStyle={customStyle}/>}
        </div>
    );
}

