package Project.SangCom.util.exception;

/**
 * @Author : Jeeseob
 * @CreateAt : 2022/11/25
 */

public class BusinessException extends RuntimeException {
    public BusinessException() {
        super();
    }

    public BusinessException(ExMessage exMessage) {
        super(exMessage.getMessage());
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }
}

/**
 * 예외 처리를 하는 부분
 * 비지니스 익셉션이 발생하면 핸들러에서 알아서 받아서 알려준다.
 */