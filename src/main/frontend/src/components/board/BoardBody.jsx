import React from 'react';
import { useState, useEffect, useMemo } from 'react';

import BoardTitle from './BoardTitle';
import BestArticle from './BestArticle';
import ArticlePreview from './ArticlePreview';

import { authInstance } from '../../utility/api';
import styles from './BoardBody.module.css';
import { MdKeyboardArrowDown } from 'react-icons/md';


export default function BoardBody({ category }) {
    return (
        <div className={styles.wrapper}>
            <BoardTitle category={category} />
            <BestArticle api={`api/board/${category}/best`}/>
            <Previews api={`api/board/${category}/list`}/>
        </div>
    );
}

function Search() {
    return (
        <div>
            
        </div>    
    );
}

function Previews({ api }) {
    const [page, setPage] = useState(0);
    const [articles, setArticles] = useState({});
    const [isNewArticleExist, setIsNewArticleExist] = useState(true);

    const handleClickBtn = async () => {
        const getArticles = await (await authInstance.get(`${api}/?page=${page + 1}`)).data.data;

        if(!getArticles.empty) {
            setArticles((prev) => [...prev, ...getArticles.content]);
            setPage((prev) => prev + 1);
        }
        getArticles.last && setIsNewArticleExist(false);
    };

    useEffect(() => {
        authInstance.get(api)
        .then(function (res) { return res.data.data })
        .then(function (data) { 
            setArticles(data.content);
            data.last && setIsNewArticleExist(false);
        })
    }, []);

    const memoizedArticles = useMemo(() => {
        return Object.values(articles).map((article)=> {
            return <ArticlePreview articleInfo={article} key={article.id}/>
        })
    }, [articles])

    return (
        <>
            <div className={styles.previews}>
                {memoizedArticles}
            </div>
            {isNewArticleExist && <button className={styles.addBtn} onClick={handleClickBtn}>더보기<MdKeyboardArrowDown/></button>}
        </>
    );
}
