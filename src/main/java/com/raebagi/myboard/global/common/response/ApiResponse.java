package com.raebagi.myboard.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"success","code", "message", "data"})
public class ApiResponse<T> {

	private final boolean success;
	private final String code;
	private final String message;
	private final T data;

	//본문 있는 성공 응답
	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true,"ok","요청이 성공했습니다." ,data);
	}

	//본문 없는 성공 응답
	public static <T> ApiResponse<T> successNodata(String code, String message) {
		return new ApiResponse<>(true,code, message,null);
	}

	//실패 응답
	public static <T> ApiResponse<T> fail(String code, String message) {
		return new ApiResponse<>(false,code,message,null);
	}


}
