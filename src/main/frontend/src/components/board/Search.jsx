import React, { useRef, useState } from 'react';
import { useEffect } from 'react';
import { MdSearch } from 'react-icons/md';
import { useParams } from 'react-router-dom';
import styles from './Search.module.css';

export default function Search({ category, boardTitle }) {
    const [selection, setSelect] = useState("all");
    const [search, setSearch] = useState("");
    const inputRef = useRef();
    const params = useParams();

    const handleSubmit = (e) => {
        e.preventDefault();
        window.location.href = `/board/${category}/${selection}/${search}`;
    }

    useEffect(() => {
        if(params.search) {
            inputRef.current.defaultValue = `${params.search}`;
            setSelect(params.selection);
            setSearch(params.search);
        }
        else{
            inputRef.current.value = search;
        }
    }, []);

    return (
        <div className={styles.wrapper}>
            <div className={styles.search}>
                <form 
                    onSubmit={handleSubmit}
                    className={styles.form}
                >
                    <select
                        className={styles.select}
                        value={`${selection}`}
                        onChange={(e) => { setSelect(e.target.value) }}>
                        <option value="all">제목+내용</option>
                        <option value="title">제목</option>
                        <option value="content">내용</option>
                    </select>
                    <div className={styles.divider}></div>
                    <input
                        ref={inputRef}
                        className={styles.input}
                        onChange={(e) => { setSearch(e.target.value) }}
                        placeholder={`${boardTitle}에서 검색하기`}
                        required
                    />
                    <button type='submit' className={styles.submitBtn}>
                        <MdSearch />
                    </button>
                </form>
            </div>
        </div>
    );
}

