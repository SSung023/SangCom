package Project.SangCom.util.response.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author : Jeeseob
 * @CreateAt : 2022/10/04
 */

@Getter
@Setter
public class CommonResponse {
    private int code;
    private String message;
}

/**
 * 0: 성공, 1: 실패
 * 실패인 경우 예외 처리 메세지도 같이 전달
 * 0 성공은 insert 결과 같은 것
 */