package com.raebagi.myboard.user.domain.entity;

public enum UserRoleEnum {

	ADMIN("ROLE_ADMIN"),
	USER("ROLE_USER");

	private final String authority;

	public String getAuthority() {
		return this.authority;
	}

	// 생성자: enum 상수가 만들어질 때 호출
	// enum 상수의 authority와 description 초기화
	UserRoleEnum(String authority) {
		this.authority = authority;
	}
}


