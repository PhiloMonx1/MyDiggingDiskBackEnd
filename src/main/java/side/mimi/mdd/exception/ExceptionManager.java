package side.mimi.mdd.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionManager {
	@ExceptionHandler(AppException.class)
	public ResponseEntity<?> appExceptionHandler(AppException error){
		ErrorCode errorCode = error.getErrorCode();
		HttpStatus httpStatus = errorCode.getHttpStatus();
		String message = error.getMessage();

		Map<String, Object> responseBody = new LinkedHashMap<>();
		responseBody.put("HttpStatusCode", httpStatus.value());
		responseBody.put("HttpStatusMessage", httpStatus.getReasonPhrase());
		responseBody.put("ErrorCode", errorCode.name());
		responseBody.put("errorMessage", message);

		return ResponseEntity.status(httpStatus).body(responseBody);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<?> runtimeExceptionHandler(RuntimeException error){
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(error.getMessage());
	}
}
