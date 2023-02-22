import React, { useState } from 'react';
import { authInstance } from '../../utility/api';
import styles from './CreateComments.module.css';
import { MdSend } from 'react-icons/md';

// parentId는 일반적인 경우 ""로 초기화
export default function CreateComments({ category, parentId, articleId }) {
    const api = parentId ? `api/board/${category}/${articleId}/comment/${parentId}` : `api/board/${category}/${articleId}/comment`;
    
    const customStyle = {
        border: `none`,
        backgroundColor: `var(--btn-bg-color)`,
        boxShadow: `none`
    };

    const [ form, setForm ] = useState({
        parentId: parentId,
        authorName: "",
        content: "",
        isAnonymous: false*1
    })

    const postComment = (url) => {
        console.log(form);
        authInstance.post(`${url}`, { ...form })
        .then(function (res) {
            console.log(res.data);
        })
        .catch(function (err) {
            console.log(err);
            alert("댓글 작성이 완료되지 않았습니다. 잠시 후에 다시 시도해 주세요.")
        })
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        if(form.content !== ""){
            postComment(api);
            return;
        }
    };

    return (
        <div className={styles.wrapper} style={parentId && customStyle}>
            <form
                onSubmit={handleSubmit}
                className={`${styles.form}`}
            >
                <input 
                    type="checkbox"
                    id="anonymous"
                    className={`${styles.anonymous}`}
                    checked={form.isAnonymous}
                    onChange={(e) => { setForm({...form, isAnonymous: e.target.checked * 1}) }}
                />
                <label htmlFor="anonymous" className={`${styles.label}`}>익명</label>
                
                <div className={`${styles.verticalDivider}`} style={parentId && {backgroundColor: 'var(--white-color)'}}></div>
                
                <textarea
                    type="text"
                    className={`${styles.content}`}
                    value={form.content}
                    placeholder={parentId ? '대댓글을 입력하세요' : '댓글을 입력하세요'}
                    autoComplete='off'
                    onChange={(e) => { setForm({...form, content: e.target.value}) }}
                    style={parentId && {backgroundColor: 'var(--btn-bg-color)'}}
                />
                
                <button type="submit" className={`${styles.submitBtn}`}><MdSend /></button>
            </form>
        </div>
    );
}

