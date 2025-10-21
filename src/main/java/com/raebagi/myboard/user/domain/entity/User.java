package com.raebagi.myboard.user.domain.entity;

import com.raebagi.myboard.global.common.entity.BaseEntity;
import com.raebagi.myboard.user.presentation.dto.request.GetUpdateReq;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 접근 수준을 PROTECTED로 설정
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String nickname;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private UserRoleEnum role;

	@Builder
	public User(String nickname, String email, String password) {
		this.nickname = nickname;
		this.email = email;
		this.password = password;
	}

	public void updateProfile(GetUpdateReq updateReq, String encodedPassword) {
		if (!this.email.equals(updateReq.email())) {
			this.email = updateReq.email();
		}
		if (!this.nickname.equals(updateReq.nickname())) {
			this.nickname = updateReq.nickname();
		}
		if (!this.password.equals(encodedPassword)) {
			this.password = encodedPassword;
		}
	}
}
