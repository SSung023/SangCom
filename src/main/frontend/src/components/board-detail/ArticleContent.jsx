import React, {useEffect, useState} from "react";
import styles from './ArticleDetail.module.css';
import defaultProfile from '../../images/defualtProfile.svg';
import {
    MdChatBubbleOutline,
    MdFavoriteBorder,
    MdOutlineFavorite,
    MdBookmarkBorder,
    MdOutlineBookmark
} from "react-icons/md";
import {authInstance} from "../../utility/api";
import {articleLike, articleDelete, articleScrap, articleUnscrap} from "../../utility/ArticleApi";
import ArticleEdit from "../board-detail/ArticleEdit";
import EditModal from "../ui/EditModal";


export default function ArticleContent({ articleInfo, scrapInfo }){
    const [article, setArticle] = useState(() => articleInfo);
    const [scrapIcon, setScrapIcon] = useState();
    const [scrap, setScrap] = useState(() => scrapInfo);
    const [scrapStatus, setScrapStatus] = useState("");
    const [modal, setModal] = useState(false);
    const currentBoard = article.boardCategory.toLowerCase();
    const createdTime = new Date(article.createdDate);

    const timestamp = (createdDate) => {
        const dformatter = new Intl.DateTimeFormat('ko', {dateStyle: 'short', timeStyle: 'short'});
        return dformatter.format(createdDate);
    }

    const handleArticleLike = async () => {
        articleLike(article.id, 0, 0)
            .then(function(data) {
                console.log(data.data);
                setArticle(data.data);
            })
    }

    const handleArticleDelete = async () => {
        if (window.confirm("게시글을 삭제하시겠습니까?")) {
            articleDelete(article.id)
                .then(function (data) {
                    return window.location.replace(`/board/${currentBoard}`);
                })

        }
    }


    const handleArticleScrap = async () => {
        console.log(scrapInfo)
        if(!scrapIcon && window.confirm("게시글을 스크랩 하시겠습니까?")){
            setScrapIcon(!scrapIcon)
            articleScrap(article.id)
                .then(function (data){
                    console.log(scrapInfo)
                    console.log(data.data); //스크랩 상태
                })
                .catch(function (err){
                    console.log(err);
            })
        }
        else if(scrapIcon && window.confirm("게시글 스크랩을 취소하시겠습니까?")){
            setScrapIcon(!scrapIcon)
            articleUnscrap(article.id)
                .then(function (data){
                    console.log(data.data); // 스크랩 상태
                })
                .catch(function(err){
                    console.log(err);
                })
        }
    }

    /*const handleArticleScrap = async () => {
        if (window.confirm("게시글을 스크랩하시겠습니까?")) {
            articleScrap(article.id)
                .then(function (data) {
                    console.log(scrap.data);
                    console.log(data.data.status); // 요청 확인
                    setScrapStatus(data.data.status);
                    if (data.data.status === "CONFLICT") { // 스크랩이 되어 있는 경우 다시 누르면
                        articleUnscrap(article.id) // 스크랩 취소
                            .then(function (data) {
                                console.log(`article ID ${article.id} scrap cancelled`);
                            })
                            .catch(function (err) {
                                console.log(err);
                            })
                    }
                })
        }
    }*/

    /* useEffect(() => { // 스크랩 정보 확인
        authInstance.get(`/api/scrap`)
            .then(function(res){
            })
    })*/

    return(
        <div className={styles.wrapper}>
            <div className={styles.article}>
                <div className={styles.articleInfo}>
                    <img className={styles.photo} src={defaultProfile} alt=""/>
                    <div className={styles.info}>
                        <div className={styles.userName}>{article.author}</div>
                        <div>{timestamp(createdTime)}</div>
                    </div>
                </div>
                <Content className={styles.content} article={article}/>
                <ResponseInfo isLike={article.isLikePressed} likeCnt={article.likeCount} commentCnt={article.commentCount}
                              scrapIcon={scrapIcon}/>
                <div className={styles.articleButtons}>
                    <button onClick={handleArticleLike}>좋아요</button>
                    {article.isOwner ? null : <button onClick={handleArticleScrap}>스크랩</button>}
                    {article.isOwner ? <button onClick={handleArticleDelete}>삭제</button> : null}

                    {article.isOwner ? <button onClick={()=>{setModal(!modal)}}>수정</button> : null}
{/*
                    {modal === true ? <EditModal iconName="MdCreate" feature={"글을 작성하세요!"}><ArticleEdit category={article?.category}/></EditModal> : null}
*/}
                </div>
                {!(article.isOwner) && <button className={styles.report}>신고</button>}
            </div>
        </div>
    )
}

function Content({ article }){
    return(
        <div>
            <div className={styles.title}>{article.title}</div>
            <div className={styles.content}>{article.content}</div>
        </div>
    )
}


function ResponseInfo({ isLike, likeCnt, commentCnt, scrapIcon, scrapCnt }){
    return (
        <div className={styles.responseInfo}>
            {isLike ? <MdOutlineFavorite/> : <MdFavoriteBorder/>}
            <p>{likeCnt}</p>
            <MdChatBubbleOutline/>
            <p>{commentCnt}</p>
            {scrapIcon === true ? <MdOutlineBookmark/>:<MdBookmarkBorder/>}
            <p>{scrapCnt}</p>
        </div>
    );
}