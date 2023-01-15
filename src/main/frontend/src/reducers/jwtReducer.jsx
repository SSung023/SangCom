export const SET_TOKEN = "jwt/SET_TOKEN";

export const setTokenAction = (token) => ({ type: SET_TOKEN, token });

const initialState = ""; // NULL

// jwt accesstoken을 관리하는 reducer 
const loginReducer = ( state = initialState, action ) => {
    switch(action.type) {
        case SET_TOKEN:
            return action.token ? action.token : "토큰이 유효하지 않습니다.";
        default:
            return state;
    }
};

export default loginReducer;