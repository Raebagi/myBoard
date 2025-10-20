package com.raebagi.myboard.global.common.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.raebagi.myboard.global.common.code.BaseErrorCode;
import com.raebagi.myboard.global.common.code.ClientErrorCode;
import com.raebagi.myboard.global.common.code.ServerErrorCode;
import com.raebagi.myboard.global.common.exception.BoardBizException;
import com.raebagi.myboard.global.common.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	// BoardBizException 처리
	@ExceptionHandler(BoardBizException.class)
	public ResponseEntity<ApiResponse<Void>> handlerBoardBizException(BoardBizException ex) {
		BaseErrorCode errorCode = ex.getErrorCode();
		log.warn("BoardBizException occurred: code={}, message={}", errorCode.getCode(), errorCode.getMessage(), ex);
		return commonErrorResponse(errorCode);
	}

	//예상치 못한 모든 예외 처리
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handlerException(Exception ex) {
		log.error("Unexpected error occurred", ex);
		ServerErrorCode errorCode = ServerErrorCode.INTERNAL_SERVER_ERROR;
		return commonErrorResponse(errorCode);
	}

	//DTO 검증 실패 처리
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		ClientErrorCode errorCode = ClientErrorCode.INVALID_INPUT_VALUE;

		String validationMessage = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.findFirst()
			.map(FieldError::getDefaultMessage)
			.filter(StringUtils::hasText)
			.orElse("");

		log.warn("validation failed : {}", validationMessage);
		return ResponseEntity.badRequest()
			.body(ApiResponse.fail(errorCode.getCode(), validationMessage));
	}

	//공통 실패 응답 에러 코드
	public ResponseEntity<ApiResponse<Void>> commonErrorResponse(BaseErrorCode errorCode) {
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ApiResponse.fail(errorCode.getCode(), errorCode.getMessage()));
	}

	// 커스텀 메시지 실패 응답 에러 코드
	public ResponseEntity<ApiResponse<Void>> commonErrorResponse(BaseErrorCode errorCode, String message) {
		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ApiResponse.fail(errorCode.getCode(), message));
	}
}

