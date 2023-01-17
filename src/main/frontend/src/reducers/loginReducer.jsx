export const LOGIN = "login/LOGIN";
export const SET_TOKEN = "jwt/SET_TOKEN";
export const DELETE_TOKEN = "jwt/DELETE_TOKEN";

export const setTokenAction = (token) => ({ type: SET_TOKEN, token });
export const deleteTokenAction = () => ({ type: DELETE_TOKEN });
export const loginAction = (userInfos) => ({ type: LOGIN, userInfos });

const initialState = {
    user: {
        access_token: "",
        info: {
            role: "",
            username: "",
            nickname: "",
            grade: "",
            classes: "",
            number: "",
            chargeSubject: "",
            chargeGrade: ""
        }
    }
};

// 로그인 가능한 사용자의 정보를 store에 저장하는 reducer
const loginReducer = ( state = initialState, action ) => {
    switch(action.type) {
        case SET_TOKEN:
            return {...state, user: {...state.user, access_token: `${action.token}`}};
        case DELETE_TOKEN:
            return {...state, user: {...state.user, access_token: ""}};
        case LOGIN:
            console.log(action.userInfos);
            return {...state, user: {...state.user, info: action.userInfos}};
        default:
            return state;
    }
};

export default loginReducer;