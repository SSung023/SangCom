import React from 'react';
import { useState, useEffect, useRef } from 'react';
import ArticlePreview from '../components/board/ArticlePreview';
import BestArticle from '../components/board/BestArticle';
import { authInstance } from '../utility/api';
import styles from './BoardBodyLayout.module.css';

export default function BoardBodyLayout({ boardTitle, bestApi, listApi }) {
    return (
        <div className={styles.wrapper}>
            {/* 
                1. BoardTitle 
                2. BestArticle
                3. Search
                4. Previews
                    ㄴ ArticlePreview
                    ㄴ ArticlePreview
                    ㄴ ArticlePreview
                    ㄴ MoreButton
            */}
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
    const [loading, setLoading] = useState(false);
    const [page, setPage] = useState(0);
    const [articles, setArticles] = useState({
        title: "",
        content: "",
        author: "",
        id: 0,
        isAnonymous: 0,
        isLikePressed: 0,
        isOwner: 0,
        likeCount: 0,
    });

    const previewsRef = useRef();

    const handleClickBtn = async () => {
        setLoading(true);
        const getArticles = await (await authInstance.get(`${api}/?page=${page + 1}`)).data.data;
        
        setArticles(getArticles);
        setPage((prev) => prev + 1);
    };

    useEffect(() => {
        setLoading(true);
        
        authInstance.get(api)
        .then(function (res) { console.log(res) })
        //.then(function (data) { setArticles(data) })
    }, []);

    useEffect(() => {
        setLoading(false);
        console.log(articles);
    }, [articles]);

    return (
        <>
            <div className={styles.previews} ref={previewsRef}>
                {/* 여기서 받아온 페이지 숫자 만큼 ArticlePreview 컴포넌트를 추가. */}
            </div>
            {console.log(articles)}
            {/* {articles.content.length != 0 && <button onClick={handleClickBtn}>더보기</button>} */}
        </>
        
    );
}
