import React, { useState } from 'react';
import { MdSearch } from 'react-icons/md';
import { authInstance } from '../../utility/api';
import styles from './Search.module.css';

export default function Search({ category, boardTitle }) {
    const [selection, setSelect] = useState("title");
    const [search, setSearch] = useState("");
    const onChange = (e) => {
        setSearch(e.target.values);
    };
    const handleSubmit = (e) => {
        e.preventDefault();
        authInstance.get(`/api/board/${category}/search?query=${selection}&keyword=${search}&page=0`)
        .then(function(res) {
            console.log(res.data);
        })
    }

    return (
        <div className={styles.search}>
            <form 
                onSubmit={handleSubmit}
                className={styles.form}
            >
                <select
                    className={styles.select}
                    defaultValue="all" 
                    onChange={(e) => { setSelect(e.target.value) }}>
                    <option value="all">제목+내용</option>
                    <option value="title">제목</option>
                    <option value="content">내용</option>
                </select>
                <div className={styles.divider}></div>
                <input
                    className={styles.input}
                    onChange={(e) => { setSearch(e.target.value) }} 
                    placeholder={`${boardTitle}에서 검색하기`}
                />
                <button type='submit' className={styles.submitBtn}>
                    <MdSearch />
                </button>
            </form>
        </div>    
    );
}

