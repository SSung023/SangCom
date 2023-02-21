import React, { useEffect, useMemo, useState } from 'react';
import { MdKeyboardArrowDown } from 'react-icons/md';
import { useParams } from 'react-router-dom';
import { authInstance } from '../../utility/api';
import ArticlePreview from './ArticlePreview';
import styles from './Previews.module.css';

export default function Previews() {
    const [page, setPage] = useState(0);
    const [articles, setArticles] = useState({});
    const [isNewArticleExist, setIsNewArticleExist] = useState(true);

    const params = useParams();
    
    const api = () => {
        return params.search ? 
        `/api/board/${params.category}/search?query=${params.selection}&keyword=${params.search}&page=`
        : `/api/board/${params.category}/list/?page=`;
    };

    const handleClickBtn = async () => {
        const getArticles = await (await authInstance.get(`${api()}${page + 1}`)).data.data;

        if(!getArticles.empty) {
            setArticles((prev) => [...prev, ...getArticles.content]);
            setPage((prev) => prev + 1);
        }
        getArticles.last && setIsNewArticleExist(false);
    };

    useEffect(() => {
        console.log(api());
        authInstance.get(api())
        .then(function (res) { return res.data.data })
        .then(function (data) { 
            setArticles(data.content);
            data.last && setIsNewArticleExist(false);
        })
    }, []);

    const memoizedArticles = useMemo(() => {
        if(articles.length == 0){
            return <div 
                        style={{ 
                            fontSize: `14px`, 
                            width: `fit-content`,
                            margin: `30px auto auto auto`,
                            fontWeight: `var(--bold)`,
                            color: `var(--light-txt-color)`
                        }}
                    >💧 일치하는 결과가 없어요</div>
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
            {isNewArticleExist && <button className={styles.addBtn} onClick={handleClickBtn}>더보기<MdKeyboardArrowDown/></button>}
        </>
    );
}

