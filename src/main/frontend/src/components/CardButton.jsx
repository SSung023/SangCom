import React from 'react';

export default function CardButton({ title }) {
    return (
        <button type="button" className='card-btn'>
            { title }
        </button>
    );
}

