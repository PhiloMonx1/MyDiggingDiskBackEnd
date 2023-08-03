package side.mimi.mdd.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
	/**
	 *MemberService 관련 에러코드
	 */
	//조회 (Get)
	NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, ""),
	//가입 (Join)
	EMPTY_JOIN_REQUEST(HttpStatus.BAD_REQUEST, ""),
	MEMBER_NAME_DUPLICATED(HttpStatus.CONFLICT, ""),
	MEMBER_NICKNAME_DUPLICATED(HttpStatus.CONFLICT, ""),
	WRONG_MEMBER_NAME_VALID(HttpStatus.BAD_REQUEST, ""),
	WRONG_PASSWORD_VALID(HttpStatus.BAD_REQUEST, ""),
	WRONG_NICKNAME_VALID(HttpStatus.BAD_REQUEST, ""),
	WRONG_INTRODUCE_VALID(HttpStatus.BAD_REQUEST, ""),
	//로그인 (Login)
	MEMBER_NAME_NOT_FOUND(HttpStatus.NOT_FOUND, ""),
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "")

	;

	private HttpStatus httpStatus;
	private String message;
}