package com.baro.barointern.domain.user.entity;

import com.baro.barointern.global.enums.UserRole;
import lombok.Getter;

@Getter
public class User {
	private Long id;
	private String userName;
	private String userNickname;
	private String password;
	private UserRole role;

	public User(Long id, String userName, String userNickname, String password, UserRole role) {
		this.id = id;
		this.userName = userName;
		this.userNickname = userNickname;
		this.password = password;
		this.role = role;
	}

	/**
	 * 유저 role 업데이트
	 */
	public void updateRole(UserRole role) {
		this.role = role;
	}
}
