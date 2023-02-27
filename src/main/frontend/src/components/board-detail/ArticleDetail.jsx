import React, {useEffect, useState} from "react";
import styles from './ArticleDetail.module.css';
import {authInstance} from "../../utility/api";
import ArticleContent from "./ArticleContent";

export default function ArticleDetail({ category, articleId }){
    const [article, setArticle] = useState();

    useEffect(() => {
        authInstance.get(`/api/board/${category}/${articleId}`)
            .then(function(res) {
                setArticle(res.data.data);
            })
    }, []);
    return(
        <div className={styles.wrapper}>
            {article && <ArticleContent articleInfo={article}/>}
        </div>
    )
}

