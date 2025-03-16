package com.baro.barointern.domain.user.dto;

import com.baro.barointern.domain.user.entity.User;
import java.util.List;
import lombok.Getter;

@Getter
public class SignUpResponseDto {

	private String userName;
	private String userNickname;
	private List<String> roles;

	public SignUpResponseDto(String userName, String userNickname, List<String> roles) {
		this.userName = userName;
		this.userNickname = userNickname;
		this.roles = roles;
	}

	public SignUpResponseDto() {

	}

	/**
	 * User 객체 dto 변환
	 */
	public static SignUpResponseDto toDto(User user){
		return new SignUpResponseDto(
			user.getUserName(),
			user.getUserNickname(),
			List.of(user.getRole().name())
		);
	}
}
