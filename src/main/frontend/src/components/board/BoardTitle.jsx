import React from 'react';

export default function BoardTitle({ title }) {
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

    return (
        <div style={styles}>
            {title} 
        </div>
    );
}

