package Project.SangCom.util.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @Author : Jeeseob
 * @CreateAt : 2022/11/25
 */

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	// 400 BAD_REQUEST
    UNDEFINED_ERROR(HttpStatus.BAD_REQUEST, "미정의 에러")
	,INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "파라미터 값이 유효하지 않습니다.")

	// 401 UNAUTHORIZED
	, NO_AUTHORITY(HttpStatus.UNAUTHORIZED, "접근권한이 없습니다.")
	, UNAUTHORIZED_WAY(HttpStatus.UNAUTHORIZED, "허용된 인증 방법이 아닙니다.")

	, TOKEN_INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "JWT signature이 유효하지 않습니다.")
	, TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "JWT token이 유효하지 않습니다.")
	, TOKEN_INVALID_CLAIM(HttpStatus.UNAUTHORIZED, "JWT Token의 Claim이 유효하지 않습니다.")
	, TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT token의 유효기간이 만료되었습니다.")
	, TOKEN_UNSUPPORTED(HttpStatus.UNAUTHORIZED, "지원하지 않는 token입니다.")

	// 404 NOT_FOUND 잘못된 리소스 접근
	, DATA_ERROR_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 데이터를 찾을 수 없습니다.")
	, MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "로그인 정보를 찾을 수 없습니다.")
	, SAVED_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "저장되지 않은 사용자입니다.")

	// 406 NOT_ACCEPTED?
	, ACCESS_DENIED(HttpStatus.NOT_ACCEPTABLE,"접근이 거부되었습니다.")

	// 409 CONFLICT
	, NICKNAME_DUPLICATED(HttpStatus.CONFLICT, "닉네임이 중복됩니다. 다른 닉네임으로 설정해주세요.")
	, NICKNAME_LENGTH_EXCEED(HttpStatus.CONFLICT, "닉네임의 최대 길이는 10입니다. 10 이하로 설정해주세요.")
	;




	private final HttpStatus status;
    private final String message;
}
