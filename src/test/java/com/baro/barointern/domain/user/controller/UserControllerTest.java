package com.baro.barointern.domain.user.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.baro.barointern.domain.user.dto.LoginRequestDto;
import com.baro.barointern.domain.user.dto.LoginResponseDto;
import com.baro.barointern.domain.user.dto.SignUpRequestDto;
import com.baro.barointern.domain.user.dto.SignUpResponseDto;
import com.baro.barointern.domain.user.entity.User;
import com.baro.barointern.domain.user.repository.UserRepository;
import com.baro.barointern.domain.user.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.baro.barointern.global.enums.UserRole;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
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
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	private Key secretKey;
	private String userAccessToken;
	private String adminAccessToken;
	private String expiredAccessToken;

	@BeforeEach
	void setUp() {
		//키 설정
		String secret = "t/T0dgjaudoS5d4KGuKg3HEEAwqT6RLf5udjZOXrguA=";
		this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

		//즉시 만료
		expiredAccessToken = generateToken("expiredUser", UserRole.ADMIN, 0L);

		User user = new User(1L, "userTEST", "닉네임", "testPassword", UserRole.USER);
		User admin = new User(2L, "adminTEST", "관리자닉네임", "adminPassword", UserRole.ADMIN);

		userRepository.save(user);
		userRepository.save(admin);

		//30분 토큰
		userAccessToken = generateToken(user.getUserName(), UserRole.USER, 1800L);
		adminAccessToken = generateToken(admin.getUserName(), UserRole.ADMIN, 1800L);
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
		//Given
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

		//관리자 계정 생성
		SignUpRequestDto adminRequestDto = new SignUpRequestDto();
		ReflectionTestUtils.setField(adminRequestDto, "userName", "adminTEST");
		ReflectionTestUtils.setField(adminRequestDto, "userNickname", "관리자");
		ReflectionTestUtils.setField(adminRequestDto, "password", "admin1234");

		SignUpResponseDto adminResponseDto = new SignUpResponseDto("adminTEST", "관리자", List.of("ADMIN"));

		Mockito.when(userService.signUp(Mockito.any(SignUpRequestDto.class)))
			.thenReturn(adminResponseDto);

		//When & Then
		mockMvc.perform(post("/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(adminRequestDto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.userName").value("adminTEST"));
	}

	@Test
	@DisplayName("회원가입 실패 테스트 - 이미 가입된 사용자")
	void signUpFailUserAlreadyExists() throws Exception {
		//Given
		SignUpRequestDto requestDto = new SignUpRequestDto();
		ReflectionTestUtils.setField(requestDto, "userName", "userTEST");
		ReflectionTestUtils.setField(requestDto, "userNickname", "닉네임");
		ReflectionTestUtils.setField(requestDto, "password", "12341234");

		Mockito.when(userService.signUp(Mockito.any(SignUpRequestDto.class)))
			.thenThrow(new RuntimeException("이미 가입된 사용자입니다."));

		// When & Then
		mockMvc.perform(post("/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isBadRequest()) // ✅ 400 Bad Request
			.andExpect(jsonPath("$.error.code").value("USER_ALREADY_EXISTS"))
			.andExpect(jsonPath("$.error.message").value("이미 가입된 사용자입니다."));
	}

	@Test
	@DisplayName("로그인 성공 테스트")
	void loginSuccess() throws Exception {
		//Given
		LoginRequestDto requestDto = new LoginRequestDto();
		ReflectionTestUtils.setField(requestDto, "userName", "adminTEST");
		ReflectionTestUtils.setField(requestDto, "password", "admin1234");

		Mockito.when(userService.login(Mockito.any(LoginRequestDto.class)))
			.thenReturn(new LoginResponseDto("mockToken"));

		//When & Then
		mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").exists());
	}

	@Test
	@DisplayName("로그인 실패 - 잘못된 비밀번호")
	void loginFailWrongPassword() throws Exception {
		//Given
		LoginRequestDto requestDto = new LoginRequestDto();
		ReflectionTestUtils.setField(requestDto, "userName", "userTEST");
		ReflectionTestUtils.setField(requestDto, "password", "wrongPassword");

		Mockito.when(userService.login(Mockito.any(LoginRequestDto.class)))
			.thenThrow(new AuthenticationException("INVALID_CREDENTIALS") {
			});

		//When & Then
		mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("관리자 권한 부여 성공")
	void grantAdminRoleSuccess() throws Exception {
		mockMvc.perform(patch("/admin/users/1/roles")

				.header("Authorization", "Bearer " + adminAccessToken)
				.content("{\"userRole\": \"ADMIN\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userName").value("userTEST"))
			.andExpect(jsonPath("$.userNickname").value("닉네임"))
			.andExpect(jsonPath("$.roles[0]").value("ADMIN"));
	}

	@Test
	@DisplayName("관리자 권한 부여 실패 - 일반 사용자 접근")
	void grantAdminRoleFailNotAdmin() throws Exception {

		mockMvc.perform(patch("/admin/users/1/roles")
				.header("Authorization", "Bearer " + userAccessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"userRole\": \"ADMIN\"}"))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("ACCESS_DENIED"))
			.andExpect(jsonPath("$.message").value("접근 권한이 없습니다."));

	}

	@Test
	@DisplayName("관리자 권한 부여 실패 - 존재하지 않는 사용자")
	void grantAdminRoleFailUserNotFound() throws Exception {

		mockMvc.perform(patch("/admin/users/99/roles")
				.header("Authorization", "Bearer " + adminAccessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"userRole\": \"ADMIN\"}"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("NOT_FOUND"))
			.andExpect(jsonPath("$.message").value("리소스를 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("토큰 없이 요청")
	void requestWithoutJwt() throws Exception {

		mockMvc.perform(patch("/admin/users/1/roles")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"userRole\": \"ADMIN\"}"))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value("INVALID_TOKEN"))
			.andExpect(jsonPath("$.message").value("유효하지 않은 인증 토큰입니다."));
	}

	@Test
	@DisplayName("만료된 토큰으로 요청")
	void requestWithExpiredJwt() throws Exception {

		mockMvc.perform(patch("/admin/users/1/roles")
				.header("Authorization", "Bearer " + expiredAccessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"userRole\": \"ADMIN\"}"))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value("INVALID_TOKEN"))
			.andExpect(jsonPath("$.message").value("유효하지 않은 인증 토큰입니다."));
	}

	@Test
	@DisplayName("잘못된 토큰으로 요청")
	void requestWithInvalidJwt() throws Exception {

		mockMvc.perform(patch("/admin/users/1/roles")
				.header("Authorization", "Bearer " + "INVALID.TOKEN.EXAMPLE")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"userRole\": \"ADMIN\"}"))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value("INVALID_TOKEN"))
			.andExpect(jsonPath("$.message").value("유효하지 않은 인증 토큰입니다."));
	}

}
