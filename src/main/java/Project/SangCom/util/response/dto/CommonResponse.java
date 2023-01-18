package Project.SangCom.util.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * @Author : Jeeseob
 * @CreateAt : 2022/10/04
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse {
    private HttpStatus status;
    private String message;
}

/**
 * 0: 성공, 1: 실패
 * 실패인 경우 예외 처리 메세지도 같이 전달
 * 0 성공은 insert 결과 같은 것
 */