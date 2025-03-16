package com.baro.barointern.domain.user.controller;

import com.baro.barointern.domain.user.dto.GrantAdminRoleResponseDto;
import com.baro.barointern.domain.user.dto.LoginRequestDto;
import com.baro.barointern.domain.user.dto.LoginResponseDto;
import com.baro.barointern.domain.user.dto.SignUpRequestDto;
import com.baro.barointern.domain.user.dto.SignUpResponseDto;
import com.baro.barointern.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}


	/**
	 * 회원가입 API
	 */
	@Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다." )
	@PostMapping("/signup")
	public ResponseEntity<SignUpResponseDto> signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto){

		SignUpResponseDto responseDto = userService.signUp(signUpRequestDto);

		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

	/**
	 * 로그인 API
	 */
	@Operation(summary = "로그인", description = "사용자가 로그인을 합니다." )
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto){

		LoginResponseDto responseDto = userService.login(loginRequestDto);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	/**
	 * 관리자 권한 부여 API
	 * - ADMIN role만 권한 부여 가능
	 * - userId로 특정 유저 권한 부여
	 */
	@Operation(summary = "관리자 권한 부여", description = "유저에게 관리자 권한이 부여됩니다." )
	@PatchMapping("admin/users/{userId}/roles")
	public ResponseEntity<GrantAdminRoleResponseDto> grantAdminRole(@PathVariable Long userId){

		GrantAdminRoleResponseDto responseDto = userService.grantAdminRole(userId);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}
}
