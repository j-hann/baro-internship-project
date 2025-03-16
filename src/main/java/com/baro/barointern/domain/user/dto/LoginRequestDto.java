package com.baro.barointern.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequestDto {

	@NotBlank
	private String userName;

	@NotBlank
	private String password;

}
