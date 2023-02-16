package Project.SangCom.util.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @Author : Jeeseob
 * @CreateAt : 2022/11/25
 */

@Getter
public class BusinessException extends RuntimeException {
    private HttpStatus status;

    public BusinessException() {
        super();
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = errorCode.getStatus();
    }

    public BusinessException(HttpStatus status, ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = status;
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