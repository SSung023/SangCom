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
    const token = localStorage.getItem("token");
    const instance = axios.create({ 
        baseURL: url,
        headers: {
            Authorization: `${token}`,
        },
        ...options
    });
    return instance;
}

export const defaultInstance = axiosAPI(BASE_URL);
export const authInstance = axiosAuthAPI(BASE_URL);