// TODO : NOT COMMIT!!!!!!!!!!!!!!

const CLIENT_ID = "4ff6e74791ce00ee3f567ce4c403c8de";
const REDIRECT_URI = "http://localhost:3000/login/oauth2/code/kakao";

export const KAKAO_AUTH_URL = `https://kauth.kakao.com/oauth/authorize?client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&response_type=code`;