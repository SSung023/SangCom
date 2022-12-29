package Project.SangCom.util.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @Author : Jeeseob
 * @CreateAt : 2022/11/25
 */

@Getter
@RequiredArgsConstructor
public enum ExMessage {

    UNDEFINED_ERROR("미정의에러")
	, NO_AUTHORITY("접근권한이 없습니다.")
	, ACCESS_DENIED("접근이 거부되었습니다.")
	, DATA_ERROR_NOT_FOUND("해당 데이터를 찾을 수 없습니다.")
	, SESSION_ERROR_MEMBER_NOT_FOUND("로그인 정보를 찾을 수 없습니다.")
    ;

    private final String message;
}
