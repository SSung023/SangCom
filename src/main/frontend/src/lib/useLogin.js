import { useSelector } from "react-redux";

function useLogin() {
    const actoken = useSelector((state) => state.loginReducer.user.access_token);
    return !!actoken;
}

export default useLogin;