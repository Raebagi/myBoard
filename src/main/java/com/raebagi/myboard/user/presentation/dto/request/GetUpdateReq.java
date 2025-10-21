package com.raebagi.myboard.user.presentation.dto.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

public record GetUpdateReq(
	@NotBlank
	String nickname,

	@NotBlank
	@Email(message = "이메일 형식이 올바르지 않습니다.")
	String email,

	@NotBlank(message = "비밀번호는 필수 입력입니다.")
	@Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
	String password
) {

}
