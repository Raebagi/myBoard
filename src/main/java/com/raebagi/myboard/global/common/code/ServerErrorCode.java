package com.raebagi.myboard.global.common.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServerErrorCode implements BaseErrorCode{
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "OPPTY-SRV-500-01", "서버 내부 오류가 발생했습니다."),
	DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "OPPTY-SRV-500-02", "데이터베이스 오류가 발생했습니다."),
	EXTERNAL_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "OPPTY-SRV-500-03", "외부 서비스 호출 오류가 발생했습니다.");

	private final HttpStatus httpStatus;
	private final String message;
	private final String code;
}
