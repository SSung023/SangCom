package Project.SangCom.util.response.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
}
