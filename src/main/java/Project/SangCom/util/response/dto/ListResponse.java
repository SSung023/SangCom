package Project.SangCom.util.response.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * @Author : Jeeseob
 * @CreateAt : 2022/10/04
 */

@Getter
@RequiredArgsConstructor
public class ListResponse<T> extends CommonResponse {
    private List<T> dataList;
    private int count;

    public ListResponse(List<T> dataList) {
        this.dataList = dataList;
        this.count = dataList.size();
    }

    public ListResponse(HttpStatus status, String message, List<T> data) {
        super(status, message);
        this.dataList = data;
    }
}

/**
 * 데이터가 몇개 있는지 + 데이터를 전달하니까
 * 프론트 단에서 처리할 때 바로 받아서 처리할 수 있게 된다.
 */