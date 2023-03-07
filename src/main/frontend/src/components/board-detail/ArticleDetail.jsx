import React, {useEffect, useState} from "react";
import styles from './ArticleDetail.module.css';
import {authInstance} from "../../utility/api";
import ArticleContent from "./ArticleContent";

export default function ArticleDetail({ category, articleId }){
    const [article, setArticle] = useState();
    const [scrap, setScrap] = useState();

    useEffect(() => {
        authInstance.get(`/api/board/${category}/${articleId}`)
            .then(function(res) {
                setArticle(res.data.data);
            })
    }, []);

    useEffect(() => {
        authInstance.get(`/api/scrap`)
            .then(function(res){
                setScrap(res.data);
            })
    })

    return(
        <div className={styles.wrapper}>
            {article && scrap && <ArticleContent articleInfo={article} scrapInfo={scrap}/>}
        </div>
    )
}

