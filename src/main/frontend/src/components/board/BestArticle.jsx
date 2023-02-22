import React, { useState } from 'react';
import { useEffect } from 'react';
import { authInstance } from '../../utility/api';
import ArticlePreview from './ArticlePreview';
import styles from './BestArticle.module.css';

export default function BestArticle({ api }) {
    const [article, setArticle] = useState();
    const customStyle = {
        backgroundColor: `var(--btn-bg-color)`,
        borderRadius: `10px`,
        border: `none`
    };
    
    useEffect(() => {
        authInstance.get(api)
        .then(function(res) {
            setArticle(res.data.data);
        })
    }, [api]);

    return (
        <div className={styles.wrapper}>
            <div className={styles.title}>실시간 인기글</div>

            { article ? <ArticlePreview articleInfo={article} customStyle={customStyle}/> : <None />}
        </div>
    );
}

function None() {
    return (
        <div className={styles.none}>
            아직 실시간 인기글이 없어요 🫠
        </div>
    );
}

