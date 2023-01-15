export const LOGIN = "login/LOGIN";

export const loginAction = (userInfos) => ({ type: LOGIN, userInfos });

const initialState = {
    user: {
        info: {
            role: "",
            nickname: "",
            grade: "",
            class: "",
            number: "",
            subject: "",
            targetGrade: "", 
        }
    }
}; //NULL

// 로그인 가능한 사용자의 정보를 store에 저장하는 reducer
const loginReducer = ( state = initialState, action ) => {
    switch(action.type) {
        case LOGIN:

        default:
            return state;
    }
};

export default loginReducer;