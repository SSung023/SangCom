import React from 'react';

export default function BoardTitle({ category }) {
    const styles = {
        boxSizing: `border-box`,
        width: `100%`,
        fontSize: `18px`,
        fontWeight: `var(--bold)`,

        color: `var(--txt-color)`,
        backgroundColor: `var(--white-color)`,
        border: `1px solid var(--line-color)`,
        padding: `0.8em 1em 0.8em 1em`,
    }

    const setTitle = () => {
        switch (category) {
            case 'free':
                return '자유 게시판'
            case 'grade1':
                return '1학년 게시판'
            case 'grade2':
                return '2학년 게시판'
            case 'grade3':
                return '3학년 게시판'
            case 'council':
                return '학생회 공지 게시판'
            case 'suggestion':
                return '학생회 건의 게시판'
            case 'club':
                return '동아리 게시판'
            default:
                break;
        }
    }

    return (
        <div style={styles}>
            {setTitle()} 
        </div>
    );
}

