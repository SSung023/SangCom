import React, { useEffect, useMemo, useState } from 'react';
import { MdKeyboardArrowDown } from 'react-icons/md';
import { useParams } from 'react-router-dom';
import { authInstance } from '../../utility/api';
import ArticlePreview from './ArticlePreview';
import styles from './Previews.module.css';

export default function Previews({ category }) {
    const [page, setPage] = useState(0);
    const [articles, setArticles] = useState({});
    const [isNewArticleExist, setIsNewArticleExist] = useState(true);

    const params = useParams();

    const api = ( page ) => {
        return params.search ? 
        `/api/board/${category}/search?query=${params.selection}&keyword=${params.search}&page=${page}`
        : `/api/board/${category}/list/?page=${page}`;
    };

    const handleClickBtn = async () => {
        const getArticles = await (await authInstance.get(`${api(page + 1)}`)).data.data;

        if(!getArticles.empty) {
            setArticles((prev) => [...prev, ...getArticles.content]);
            setPage((prev) => prev + 1);
        }
        getArticles.last && setIsNewArticleExist(false);
    };

    useEffect(() => {
        setPage(0);
        authInstance.get(api())
        .then(function (res) { return res.data.data })
        .then(function (data) { 
            setArticles(data.content);
            data.last ? setIsNewArticleExist(false) : setIsNewArticleExist(true);
        })
    }, [category]);

    const memoizedArticles = useMemo(() => {
        if(articles.length == 0){
            if(params.search){
                return <div style={{ 
                    fontSize: `14px`, 
                    width: `fit-content`,
                    margin: `30px auto auto auto`,
                    fontWeight: `var(--bold)`,
                    color: `var(--light-txt-color)`}}
                >일치하는 결과가 없어요 💧</div>
            }
            return <div style={{ 
                        display: `flex`,
                        flexDirection: `column`,
                        alignItems: `center`,
                        fontSize: `14px`, 
                        width: `fit-content`,
                        margin: `30px auto auto auto`,
                        fontWeight: `var(--bold)`,
                        color: `var(--light-txt-color)`}}
                    ><p>아직 게시글이 존재하지 않아요</p><p>글을 작성해보세요 🌟</p></div>
        }
        else {
            return Object.values(articles).map((article)=> {
                return <ArticlePreview articleInfo={article} key={article.id}/>
            })
        }
    }, [articles])

    return (
        <>
            <div className={styles.previews}>
                {memoizedArticles}
            </div>
            {isNewArticleExist &&
            <div className={styles.addBtnBox}>
                <div className={styles.previewBox}></div>
                <button className={styles.addBtn} onClick={handleClickBtn}>
                    더보기<MdKeyboardArrowDown/>
                </button>
            </div>
            }
        </>
    );
}

