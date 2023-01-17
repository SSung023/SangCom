export const SET_TOKEN = "jwt/SET_TOKEN";
export const DELETE_TOKEN = "jwt/DELETE_TOKEN";

export const setTokenAction = (token) => ({ type: SET_TOKEN, token });

const initialState = {
    access_token: ""
};

// jwt accesstoken을 관리하는 reducer 
const jwtReducer = ( state = initialState, action ) => {
    switch(action.type) {
        case SET_TOKEN:
            return {...state, access_token: `${action.token}`};
        case DELETE_TOKEN:
            return {...state, access_token: ""};
        default:
            return state;
    }
};

export default jwtReducer;