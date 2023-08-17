package side.mimi.mdd.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
	/**
	 *JWT 관련 에러코드
	 */
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료시간이 지난 토큰입니다."),
	NOT_DECODE_TOKEN(HttpStatus.UNAUTHORIZED, "토큰을 decode 할 수 없습니다."),
	WRONG_TYPE_TOKEN(HttpStatus.UNAUTHORIZED, "알아볼 수 없는 토큰입니다."),
	BLACKLIST_TOKEN(HttpStatus.UNAUTHORIZED, "사용할 수 없는 리프레시 토큰입니다."),
	/**
	 *MemberService 관련 에러코드
	 */
	//조회 (Get)
	NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "해당 맴버를 찾을 수 없습니다."),
	//가입 (Join)
	EMPTY_JOIN_REQUEST(HttpStatus.BAD_REQUEST, "MemberName, password은 필수 값 입니다."),
	MEMBER_NAME_DUPLICATED(HttpStatus.CONFLICT, "이미 사용중인 MemberName 입니다."),
	MEMBER_NICKNAME_DUPLICATED(HttpStatus.CONFLICT, "이미 사용중인 nickname 입니다."),
	WRONG_MEMBER_NAME_VALID(HttpStatus.BAD_REQUEST, "MemberName은 8자 이상, 20자 이하의 영어와 숫자로만 이루어져 있어야 합니다."),
	WRONG_PASSWORD_VALID(HttpStatus.BAD_REQUEST, "password는 6자리 숫자로만 이루어져 있어야 합니다."),
	WRONG_NICKNAME_VALID(HttpStatus.BAD_REQUEST, "nickname은 빈 값이거나 10자를 초과할 수 없습니다."),
	WRONG_INTEREST_VALID(HttpStatus.BAD_REQUEST, "interest는 10자를 초과할 수 없습니다."),
	WRONG_INTRODUCE_VALID(HttpStatus.BAD_REQUEST, "introduce는 30자를 초과할 수 없습니다."),
	//로그인 (Login)
	MEMBER_NAME_NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없는 memberName 입니다."),
	INVALID_PASSWORD(HttpStatus.CONFLICT, "비밀번호가 일치하지 않습니다."),
	OVER_INVALID_PASSWORD(HttpStatus.TOO_MANY_REQUESTS, "너무 많은 로그인 시도를 했습니다. 잠시 후 다시 시도해주세요."),
	/**
	 * DiskService 관련 에러코드
	 */
	//작성
	OVER_LONG_DISK_NAME(HttpStatus.BAD_REQUEST, "DiskName 값은 빈 값이거나 30자를 초과할 수 없습니다."),
	OVER_LONG_CONTENT(HttpStatus.BAD_REQUEST, "content 값은 300자를 초과할 수 없습니다."),
	//조회
	NOT_FOUND_DISK(HttpStatus.NOT_FOUND, "해당 DiskId를 가진 Disk를 찾을 수 없습니다."),
	//수정, 삭제
	NOT_DISK_OWNER(HttpStatus.FORBIDDEN, "Disk 소유자만 수정 및 삭제 권한이 주어집니다."),
	//대표 디스크 (북마크, favorite)
	BOOKMARK_DISK_LIMIT(HttpStatus.BAD_REQUEST, "대표디스크는 3개 까지 설정 가능합니다."),
	//이미지
	IMG_COUNT_LACK(HttpStatus.BAD_REQUEST, "DISK 작성 시 최소 하나의 이미지가 필요합니다."),
	IMG_COUNT_LIMIT(HttpStatus.BAD_REQUEST, "DISK 이미지는 4개까지 설정 가능합니다."),
	NOT_FOUND_IMG(HttpStatus.NOT_FOUND, "해당 아이디의 이미지를 찾을 수 없습니다."),
	/**
	 * 이미지 유틸 관련 에러코드
	 */
	//이미지 형식 오류
	NOT_SUPPORTED_FILE_TYPE(HttpStatus.BAD_REQUEST, "MultipartFile은 현재 PNG, JPG, JPGE 타입만 지원하고 있습니다."),
	FILE_COUNT_LIMIT(HttpStatus.BAD_REQUEST, "MultipartFile은 한 번에 4개를 초과해서 등록 요청할 수 없습니다."),
	;

	private HttpStatus httpStatus;
	private String message;
}