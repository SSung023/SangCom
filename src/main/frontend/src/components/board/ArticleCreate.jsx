import React from 'react';
import { useState } from 'react';
import { authInstance } from '../../utility/api';
import styles from './ArticleCreate.module.css';

export default function ArticleCreate({ category }) {
    const [ form, setForm ] = useState({
        id: 0,
        boardCategory: category,
        authorNickname: "",
        title: "",
        content: "",
        isAnonymous: false*1
    })

    const postArticle = (url) => {
        authInstance.post(url, { ...form })
        .then(function (res) {
            console.log(res.data);
        })
        .catch(function (err) {
            console.log(err);
            alert("글 작성이 완료되지 않았습니다. 잠시 후에 다시 시도해 주세요.")
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
            <div className={`${styles.header}`}>
                <input 
                    type="checkbox"
                    id="anonymous"
                    className={`${styles.anonymous}`}
                    checked={form.isAnonymous}
                    onChange={(e) => { setForm({...form, isAnonymous: e.target.checked * 1}) }}
                />
                <label htmlFor="anonymous" className={`${styles.label}`}>익명</label>
                
                <div className={`${styles.verticalDivider}`}></div>
                
                <input 
                    type="text"
                    className={`${styles.title}`}
                    value={form.title}
                    maxLength="30"
                    placeholder='제목을 입력하세요. (최대 30자)'
                    required
                    autoComplete='off'
                    onChange={(e) => { setForm({...form, title: e.target.value}) }}
                />
            </div>
            
            <textarea 
                type="text"
                className={`${styles.content}`}
                value={form.content}
                placeholder='내용을 입력하세요.'
                autoComplete='off'
                required
                style={{ width: '100%', height: '30em'}}
                onChange={(e) => { setForm({...form, content: e.target.value}) }}
            />
            
            <button type="submit" className={`${styles.submitBtn}`}>올리기</button>
        </form>
    );
}

