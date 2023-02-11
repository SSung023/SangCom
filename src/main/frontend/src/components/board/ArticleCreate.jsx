import React from 'react';
import { useState } from 'react';
import { authInstance } from '../../utility/api';

export default function ArticleCreate({ category }) {
    
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [isAnonymous, setIsAnonymous] = useState(false);

    const handleSubmit = (e) => {
        e.preventDefault();

        const article = {
            id: 0,
            boardCategory: category,
            authorNickname: "",
            title: title,
            content: content,
            isAnonymous: isAnonymous*1
        };

        console.log(article);

        authInstance.post("api/board/free", { ...article })
        .then(function (res) {
            console.log(res.data);
        })
        .catch(function (err) {
            console.log(err);
        })
    };

    return (
        <form
            onSubmit={handleSubmit}
        >
            <label>익명
                <input 
                    type="checkbox"
                    id="anonymous"
                    name='isAnonymous'
                    checked={isAnonymous}
                    onChange={(e) => { setIsAnonymous(e.target.checked) }}
                />
            </label>
            <input 
                type="text"
                id="title"
                name='title'
                value={title}
                maxLength="30"
                onChange={(e) => { setTitle(e.target.value) }}
            />
            <textarea 
                type="text"
                id="content"
                name='content'
                value={content}
                onChange={(e) => { setContent(e.target.value) }}
            />
            <button type="submit">올리기</button>
        </form>
    );
}

