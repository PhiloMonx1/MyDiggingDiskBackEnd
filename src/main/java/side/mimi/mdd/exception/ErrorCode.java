package side.mimi.mdd.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
	//MemberService 관련 에러코드
	MEMBER_NAME_DUPLICATED(HttpStatus.CONFLICT, ""),
	MEMBER_NAME_NOT_FOUND(HttpStatus.NOT_FOUND, ""),
	INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, ""),

	;

	private HttpStatus httpStatus;
	private String message;
}


