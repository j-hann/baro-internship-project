package com.baro.barointern.domain.user.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.baro.barointern.domain.user.dto.LoginRequestDto;
import com.baro.barointern.domain.user.dto.LoginResponseDto;
import com.baro.barointern.domain.user.dto.SignUpRequestDto;
import com.baro.barointern.domain.user.dto.SignUpResponseDto;
import com.baro.barointern.domain.user.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.baro.barointern.global.enums.UserRole;
import com.baro.barointern.global.jwt.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private JwtProvider jwtProvider;


	@MockBean
	private UserService userService;

	private Key secretKey;
	private String userAccessToken;
	private String adminAccessToken;
	private String expiredAccessToken;

	@BeforeEach
	void setUp() {
		//키 설정
		String secret = "t/T0dgjaudoS5d4KGuKg3HEEAwqT6RLf5udjZOXrguA=";
		this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

		//30분 토큰
		userAccessToken = generateToken("regularUser", UserRole.USER, 1800L);
		adminAccessToken = generateToken("adminUser", UserRole.ADMIN, 1800L);

		//즉시 만료
		expiredAccessToken = generateToken("expiredUser", UserRole.ADMIN, 0L);
	}

	private String generateToken(String username, UserRole role, long expirationSeconds) {
		Date currentDate = new Date();
		Date expireDate = new Date(currentDate.getTime() + (expirationSeconds * 1000));

		return Jwts.builder()
			.setSubject(username)
			.claim("role", role.name())
			.setIssuedAt(currentDate)
			.setExpiration(expireDate)
			.signWith(secretKey, SignatureAlgorithm.HS256)
			.compact();
	}

	@Test
	@DisplayName("회원가입 성공 테스트")
	void signUpSuccess() throws Exception {
		// Given
		SignUpRequestDto requestDto = new SignUpRequestDto();
		ReflectionTestUtils.setField(requestDto, "userName", "userTEST");
		ReflectionTestUtils.setField(requestDto, "userNickname", "닉네임");
		ReflectionTestUtils.setField(requestDto, "password", "12341234");

		SignUpResponseDto responseDto = new SignUpResponseDto("userTEST", "닉네임", List.of("USER"));

		Mockito.when(userService.signUp(Mockito.any(SignUpRequestDto.class)))
			.thenReturn(responseDto);

		//when & then
		mockMvc.perform(post("/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(requestDto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.userName").value("userTEST"));
	}

	@Test
	@DisplayName("로그인 성공 테스트")
	void loginSuccess() throws Exception {
		// Given
		LoginRequestDto requestDto = new LoginRequestDto();
		ReflectionTestUtils.setField(requestDto, "userName", "userTEST");
		ReflectionTestUtils.setField(requestDto, "password", "12341234");

		Mockito.when(userService.login(Mockito.any(LoginRequestDto.class)))
			.thenReturn(new LoginResponseDto("mockToken"));

		// When & Then
		mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").exists());
	}

	@Test
	@DisplayName("로그인 실패 - 잘못된 비밀번호")
	void loginFail_WrongPassword() throws Exception {
		// Given
		LoginRequestDto requestDto = new LoginRequestDto();
		ReflectionTestUtils.setField(requestDto, "userName", "userTEST");
		ReflectionTestUtils.setField(requestDto, "password", "wrongPassword");

		Mockito.when(userService.login(Mockito.any(LoginRequestDto.class)))
			.thenThrow(new RuntimeException("INVALID_CREDENTIALS"));

		// When & Then
		mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("관리자 권한 부여 성공")
	void grantAdminRoleSuccess() throws Exception {
		// When & Then
		mockMvc.perform(patch("/admin/users/1/roles")
				.header("Authorization", "Bearer " + adminAccessToken)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("관리자 권한 부여 실패 - 일반 사용자 접근")
	void grantAdminRoleFail_NotAdmin() throws Exception {
		// When & Then
		mockMvc.perform(patch("/admin/users/1/roles")
				.header("Authorization", "Bearer " + userAccessToken)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("관리자 권한 부여 실패 - 존재하지 않는 사용자")
	void grantAdminRoleFail_UserNotFound() throws Exception {
		// Given
		Mockito.when(userService.grantAdminRole(99L))
			.thenThrow(new RuntimeException("NOT_FOUND"));

		// When & Then
		mockMvc.perform(patch("/admin/users/1/roles")
				.header("Authorization", "Bearer " + adminAccessToken)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

}
