import React from 'react';
import { useDispatch, useSelector, shallowEqual } from 'react-redux';
import { increaseCount, decreaseCount } from '../reducers/counterReducer'; // 액션 함수
import Counter from './Counter';

// shallowEqual : 객체 안에 있는 가장 겉에 있는 값만 비교(최적화)
// useSelector : 연결되어 있는 state를 특정하기 위한 훅. 전달된 state가 바뀔 때만 리렌더링한다.

export default function CounterContainer() {
    const dispatch = useDispatch();
    const count = useSelector(state => state.counter, shallowEqual); //스토어에 접근해 리듀서의 이름을 반환받아 전달해준다.

    // 각각 증가 액션과 감소 액션을 dispatch 해주는 일을 수행한다.
    const handleIncrease = (plus) => () => {
        dispatch(increaseCount(plus));
    };
    const handleDecrease = (minus) => () => {
        dispatch(decreaseCount(minus));
    }
    return (
        <div>
            <Counter onPlus={handleIncrease} onMinus={handleDecrease} />
            현재 값은 {count}입니다.
        </div>
    );
}

