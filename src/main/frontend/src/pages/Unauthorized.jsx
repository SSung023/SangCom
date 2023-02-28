import React from 'react';
import { AiFillThunderbolt } from 'react-icons/ai';

export default function NotFound() {
    const bodyStyle = {
        fontSize: '16px',
        boxSizing: 'border-box',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'flex-start',
        justifyContent: 'center',
        minHeight: '100vh',
        minWidth: '100vw',
        padding: '1em'
    }
    const infoStyle = {
        display: 'flex',
        alignItems: 'center',
        fontSize: '8em',
        fontWeight: '800'
    }

    return (
        <div style={bodyStyle}>
            <p style={{...infoStyle, color: 'var(--blue-color)'}}>
                401 
                <AiFillThunderbolt style={{marginLeft: '0.2em'}}/>
            </p>
            <span style={infoStyle}>Unauthorized</span>
        </div>
    );
}

