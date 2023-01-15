import React from 'react';
import { useState } from 'react';

export default function Counter({ onPlus, onMinus }) {
    // 내부적으로 값을 전달하기 위한 state
    const [state, setState] = useState({});

    const handleChange = (event) => {
        setState({
            ...state,
            [event.target.name] : Number(event.target.value)
        })
    };
    return (
        <div>
            {/* ?. : 옵셔널 체이닝 연산자 */}
            <input type="text" name="plus" value={state?.plus} onChange={handleChange} />
            <button onClick={onPlus(state?.plus)}>+</button>
            <input type="text" name="minus" value={state?.minus} onChange={handleChange} />
            <button onClick={onMinus(state?.minus)}>-</button>
        </div>
    );
}

