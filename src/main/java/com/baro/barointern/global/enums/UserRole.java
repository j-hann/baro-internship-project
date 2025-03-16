package com.baro.barointern.global.enums;

import java.util.Collections;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum UserRole {
	USER("유저"),
	ADMIN("관리자");

	private final String userRoleText;
	private static final String ROLE_PREFIX = "ROLE_";

	UserRole(String userRoleText) {
		this.userRoleText = userRoleText;
	}

	public List<SimpleGrantedAuthority> getAuthorities(){
		return Collections.singletonList(new SimpleGrantedAuthority(ROLE_PREFIX + this.name()));
	}

}
