package com.raebagi.myboard.global.common.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClientErrorCode implements BaseErrorCode{

	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "BOARD-CMN-400-01","입력값이 올바르지 않습니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "BOARD-CMN-401-01", "인증이 필요한 요청입니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "BOARD-CMN-403-01", "접근 권한이 없습니다."),
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "BOARD-CMN-404-01", "요청한 리소스를 찾을 수 없습니다."),
	DUPLICATE(HttpStatus.CONFLICT, "BOARD-CMN-409-01", "중복된 요청입니다.");

	private final HttpStatus httpStatus;
	private final String message;
	private final String code;
}
