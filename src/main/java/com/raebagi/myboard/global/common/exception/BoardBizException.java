package com.raebagi.myboard.global.common.exception;

import com.raebagi.myboard.global.common.code.BaseErrorCode;

import lombok.Getter;

@Getter
public class BoardBizException extends RuntimeException {

	private final BaseErrorCode errorCode;

	public BoardBizException(BaseErrorCode errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public BoardBizException(BaseErrorCode errorCode, String customMessage) {
		super(customMessage);
		this.errorCode = errorCode;
	}
}
