package com.baro.barointern.domain.user.dto;

import com.baro.barointern.domain.user.entity.User;
import java.util.List;
import lombok.Getter;

@Getter
public class GrantAdminRoleResponseDto {

	private String userName;
	private String userNickname;
	private List<String> roles;

	public GrantAdminRoleResponseDto(String userName, String userNickname, List<String> roles) {
		this.userName = userName;
		this.userNickname = userNickname;
		this.roles = roles;
	}

	public GrantAdminRoleResponseDto() {

	}

	/**
	 * User 객체 dto 변환
	 */
	public static GrantAdminRoleResponseDto toDto(User user){
		return new GrantAdminRoleResponseDto(
			user.getUserName(),
			user.getUserNickname(),
			List.of(user.getRole().name())
		);
	}
}
