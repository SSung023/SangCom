import React from 'react';

export default function CardButton(props) {
    return (
        <button type="button" className='card-btn'>
            {props.title}
        </button>
    );
}

