package Project.SangCom.util.response.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * @Author : Jeeseob
 * @CreateAt : 2022/12/01
 */
@Getter
@RequiredArgsConstructor
public class PagingResponse<T> extends CommonResponse {
    private Slice<T> data;

    public PagingResponse(Slice<T> data) {
        this.data = data;
    }

    public PagingResponse(HttpStatus status, String message, Slice<T> data) {
        super(status, message);
        this.data = data;
    }
}
