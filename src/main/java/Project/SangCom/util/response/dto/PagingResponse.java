package Project.SangCom.util.response.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @Author : Jeeseob
 * @CreateAt : 2022/12/01
 */
@Getter
@RequiredArgsConstructor
public class PagingResponse<T> extends CommonResponse {
    private Page<T> data;

    public PagingResponse(Page<T> data) {
        this.data = data;
    }
}
