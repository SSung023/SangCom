package Project.SangCom.util.formatter;

/**
 * @Author : Jeeseob
 * @CreateAt : 2022/11/25
 */
public final class CommonPattern {
    public static final String MANAGER_ID_PATTERN = "^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,16}$";
    public static final String MANAGER_PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,16}$";
    public static final String IP_ADDRESS_PATTERN = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
    public static final String Y_OR_N = "^[YN]$";
}

/**
 * 정규식에서 사용
 * 비밀 번호는 8~16자리의 숫자, 영어, 특수 기호로 이루어져야 한다
 * 아이디 : 8 ~16 사이의 A~Z + 숫자로 이루어져야 한다
 * IP 주소 형석
 * Yes or No 중에 하나
 */