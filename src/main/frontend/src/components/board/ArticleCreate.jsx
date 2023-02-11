import React from 'react';
import { useState } from 'react';
import { authInstance } from '../../utility/api';
import styles from './ArticleCreate.module.css';

export default function ArticleCreate({ category }) {
    
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [isAnonymous, setIsAnonymous] = useState(false);

    const postArticle = (url) => {
        const article = {
            id: 0,
            boardCategory: category,
            authorNickname: "",
            title: title,
            content: content,
            isAnonymous: isAnonymous*1
        };

        authInstance.post(url, { ...article })
        .then(function (res) {
            console.log(res.data);
        })
        .catch(function (err) {
            console.log(err);
        })
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        postArticle("api/board/free");
    };

    return (
        <form
            onSubmit={handleSubmit}
            className={`${styles.form}`}
        >
            <label>익명
                <input 
                    type="checkbox"
                    className={`${styles.anonumous}`}
                    checked={isAnonymous}
                    onChange={(e) => { setIsAnonymous(e.target.checked) }}
                />
            </label>
            <div className={`${styles.verticalDivider}`}></div>
            <input 
                type="text"
                className={`${styles.title}`}
                value={title}
                maxLength="30"
                placeholder='제목을 입력하세요. (최대 30자)'
                onChange={(e) => { setTitle(e.target.value) }}
            />
            <textarea 
                type="text"
                className={`${styles.content}`}
                value={content}
                onChange={(e) => { setContent(e.target.value) }}
            />
            <button type="submit">올리기</button>
        </form>
    );
}

