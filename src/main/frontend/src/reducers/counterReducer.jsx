// action type
// reducer가 많아질 떄 action 상수가 중복되는 것을 방지하기 위해
// 앞에 파일 이름을 넣는다.
export const INCREASE = "counter/INCREASE";
export const DECREASE = "counter/DECREASE";

// action 생성자
export const increaseCount = (count) => ({ type: INCREASE, count });
export const decreaseCount = (count) => ({ type: DECREASE, count });

const initialState = 0;

// Reducer는 state와 action을 받는다.
const counter = (state = initialState, action) => {
    switch(action.type) {
        case INCREASE:
            return action.count ? state + action.count : state + 1;
        case DECREASE:
            return action.count ? state - action.count : state - 1;
        // default 정의를 하지 않으면 초기값이 undefined가 된다.
        default:
            return state;    
    }
};

export default counter;