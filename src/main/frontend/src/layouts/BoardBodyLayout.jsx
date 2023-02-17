import React from 'react';
import { useMemo } from 'react';
import { useState, useEffect } from 'react';
import ArticlePreview from '../components/board/ArticlePreview';
import BestArticle from '../components/board/BestArticle';
import { authInstance } from '../utility/api';
import styles from './BoardBodyLayout.module.css';
import { MdKeyboardArrowDown } from 'react-icons/md';

export default function BoardBodyLayout({ boardTitle, bestApi, listApi }) {
    return (
        <div className={styles.wrapper}>
            <BoardTitle boardTitle={boardTitle} />
            <BestArticle api={bestApi}/>
            <Previews api={listApi}/>
        </div>
    );
}

function BoardTitle({ boardTitle }) {
    return (
        <div className={styles.boardTitle}>
            {boardTitle}
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

        // first: 첫 통신
        // empty: 받아 온 글이 없는 상태
        // last: 더 이상 받아 올 글이 없는 상태
        // console.log(`isEmpty: ${getArticles.empty}`);
        // console.log(`isLast: ${getArticles.last}`);

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
