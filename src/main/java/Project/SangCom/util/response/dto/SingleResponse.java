package Project.SangCom.util.response.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @Author : Jeeseob
 * @CreateAt : 2022/10/04
 */

@Getter
@RequiredArgsConstructor
public class SingleResponse<T> extends CommonResponse {
    private T data;

    public SingleResponse(T data) {
        this.data = data;
    }

    public SingleResponse(HttpStatus status, String message, T data) {
        super(status, message);
        this.data = data;
    }
}
