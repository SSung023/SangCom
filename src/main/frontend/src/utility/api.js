import axios from 'axios';

const BASE_URL = "http://localhost:3000";

// Auth가 필요 없는 일반 api 요청
// 사용법: import 후 defaultInstance.get('url', {});
const axiosAPI = (url, options) => {
    const instance = axios.create({ baseURL: url, ...options });
    return instance;
};

// Auth가 필요한 api 요청
// 회원가입, 로그인 일부 과정 제외하고 모두 필요함.
// 사용법: import 후 authInstance.get('url', {});
const axiosAuthAPI = (url, options) => {
    const instance = axios.create({ baseURL: url, ...options });
    return instance;
}

export const defaultInstance = axiosAPI(BASE_URL);
export const authInstance = axiosAuthAPI(BASE_URL);

// 요청시 access token을 header에 넣어서 보내주기
authInstance.interceptors.request.use(function (config) {
    const token = localStorage.getItem("token");   
    // access token이 null이면 
    config.headers["Authorization"] = token;
    return config;
});

// 요청 후 access token 만료 여부에 따라 토큰 업데이트
authInstance.interceptors.response.use(async (res) => {
    const grantType = res.headers.get("Grant-Type");
    
    if(grantType === "reissued-grant"){
        // update access token
        const newToken = res.headers.get("Authorization");
        localStorage.setItem("token", newToken);
    }
    return res;
}, (err) => {
    localStorage.setItem("token", "");
    window.location.reload(window.location.href)

    // const grantType = err.headers.get("Grant-Type");
    // console.log(grantType);
    // if(grantType === "expired"){
    //     console.log('expired');
    //     // logout
    //     localStorage.setItem("token", "");
    // }
    return err;
})