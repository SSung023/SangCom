function useLogin() {
    const actoken = localStorage.getItem("token");
    return !!actoken;
}

export default useLogin;