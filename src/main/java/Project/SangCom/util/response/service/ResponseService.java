package Project.SangCom.util.response.service;

import Project.SangCom.util.response.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author : Jeeseob
 * @CreateAt : 2022/10/04
 */

@Service
public class ResponseService {

    private void setSuccessResult(CommonResponse response) {
        response.setCode(1);
        response.setMessage("성공");
    }

    // 나중에 enum으로 바꿔보자
    private void setFailResult(CommonResponse response) {
        response.setCode(0);
        response.setMessage("실패");
    }

    public CommonResponse successResult() {
        CommonResponse response = new CommonResponse();
        this.setSuccessResult(response);

        return response;
    }

    public SingleResponse<String> failResult(String message) {
        SingleResponse<String> response = new SingleResponse<>(message);
        this.setFailResult(response);

        return response;
    }

    public <T> SingleResponse<T> singleResult(T data) {
        SingleResponse<T> response = new SingleResponse<>(data);
        this.setSuccessResult(response);

        return response;
    }

    public <T> ListResponse<T> listResult(List<T> dataList) {
        ListResponse<T> response = new ListResponse<>(dataList);
        this.setSuccessResult(response);

        return response;
    }

    public <T> PagingResponse<T> pagingResult(Page<T> data) {
        PagingResponse<T> response = new PagingResponse<T>(data);
        this.setSuccessResult(response);

        return response;
    }
}
