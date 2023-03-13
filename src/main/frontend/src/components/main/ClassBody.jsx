import React, { useCallback, useEffect, useRef, useState } from 'react';
import styles from './ClassBody.module.css';
import defaultProfile from '../../images/defualtProfile.svg';
import { BsDot } from 'react-icons/bs';
import { MdSend } from 'react-icons/md';

export default function ClassBody({ tag }) {
    const [ newPost, setPost ] = useState('');
    const [ data, setData ] = useState([
        {
            id: 1,
            author: "홍길동",
            isOwner: 0,
            title: "청춘예찬",
            content: `용기가 청춘은 인생에 따뜻한 꽃 생명을 인생을 구하지 있는가? 그들은 우리의 황금시대를 실로 인간이 말이다. 있는 같으며, 그들에게 지혜는 아니더면, 긴지라 가는 열락의 것이다. 그러므로 구하지 앞이 것은 있으며, 같으며, 열락의 봄바람이다. 우리 이상의 하였으며, 찾아다녀도, 꽃 과실이 청춘 때문이다...`,
            createDate: `2023-03-12T22:51:59.952853`
        },
        {
            id: 2,
            author: "김다은",
            isOwner: 1,
            title: "체육대회 반티",
            content: `댓글로 투표 부탁.`,
            createDate: `2023-03-12T22:51:59.952853`
        },
        {
            id: 3,
            author: "김다은",
            isOwner: 1,
            title: "체육대회 반티",
            content: `댓글로 투표 부탁.`,
            createDate: `2023-03-12T22:51:59.952853`
        },
        {
            id: 4,
            author: "김다은",
            isOwner: 1,
            title: "체육대회 반티",
            content: `댓글로 투표 부탁.`,
            createDate: `2023-03-12T22:51:59.952853`
        },
        {
            id: 5,
            author: "김다은",
            isOwner: 1,
            title: "체육대회 반티",
            content: `댓글로 투표 부탁.`,
            createDate: `2023-03-12T22:51:59.952853`
        },
        {
            id: 6,
            author: "김다은",
            isOwner: 1,
            title: "체육대회 반티",
            content: `댓글로 투표 부탁.`,
            createDate: `2023-03-12T22:51:59.952853`
        },
    ]);

    const makePostPreview = () => {
        return data.map((post) => {
            return <PostPreview post={post} key={post.id}/>
        })
    }

    const textAreaRef = useRef();
    const handleChange = useCallback((e) => {
        // e.preventDefault();
        setPost(e.target.value);
    }, []);
    
    const handleSubmit = (e) => {
        e.preventDefault();
    }

    // 줄바꿈시 text area의 높이가 늘어나게 처리
    useEffect(() => {
        if(textAreaRef.current){
            textAreaRef.current.style.height = `auto`;
            textAreaRef.current.style.height = `${textAreaRef.current.scrollHeight}px`;
        }
    }, [newPost]);
    
    return (
        <div className={styles.classBody}>
            <div className={styles.previews}>
                { data ? makePostPreview() : <None />}
            </div>

            <form
                onSubmit={handleSubmit} 
                className={styles.form}>
                <textarea 
                    rows={1}
                    className={styles.newPost} 
                    onChange={handleChange}
                    value={newPost}
                    ref={textAreaRef}
                />
                <button type="submit" className={styles.send}><MdSend /></button>
            </form>
        </div>
    );
}

function None() {
    return (
        <div className={styles.none}>
            글을 입력해보세요!
        </div>
    );
}

function PostPreview({ post }) {
    const timestamp = (createdDate) => {
        const date = new Date(createdDate)
        const dformatter = new Intl.DateTimeFormat('ko', {dateStyle: 'short', timeStyle: 'short'});
        return dformatter.format(date);
    }

    return (
        post && 
        <div className={styles.postPreview}>
            <div className={styles.profile}>
                <img src={defaultProfile} alt="프로필 사진" />
                <p className={styles.author}>{post.author}</p>
                <BsDot/>
                <p className={styles.date}>{timestamp(post.createDate)}</p>
            </div>
            
            <p className={styles.title}>{post.title}</p>
            <p className={styles.content}>{post.content}</p>
        </div>
    );
}

