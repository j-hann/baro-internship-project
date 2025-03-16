package com.baro.barointern.domain.user.service;

import com.baro.barointern.domain.user.dto.GrantAdminRoleResponseDto;
import com.baro.barointern.domain.user.dto.LoginRequestDto;
import com.baro.barointern.domain.user.dto.LoginResponseDto;
import com.baro.barointern.domain.user.dto.SignUpRequestDto;
import com.baro.barointern.domain.user.dto.SignUpResponseDto;
import com.baro.barointern.domain.user.entity.User;
import com.baro.barointern.domain.user.repository.UserRepository;
import com.baro.barointern.global.enums.UserRole;
import com.baro.barointern.global.exception.BusinessException;
import com.baro.barointern.global.exception.ExceptionType;
import com.baro.barointern.global.jwt.JwtProvider;
import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final JwtProvider jwtProvider;

	public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
		JwtProvider jwtProvider) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtProvider = jwtProvider;
	}

	/**
	 * 회원가입 서비스 메서드
	 */
	public SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto) {

		Optional<User> user = userRepository.findByName(signUpRequestDto.getUserName());

		//이미 존재하는 사용자인지 체크
		if (user.isPresent()) {
			throw new BusinessException(ExceptionType.USER_ALREADY_EXISTS);
		}

		//비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(signUpRequestDto.getPassword());

		//ID 생성
		long userId = userRepository.generateId();

		User userSign = new User(
			userId,
			signUpRequestDto.getUserName(),
			signUpRequestDto.getUserNickname(),
			signUpRequestDto.getPassword(),
			UserRole.USER
		);

		//저장
		userRepository.save(userSign);

		return new SignUpResponseDto().toDto(userSign);
	}

	/**
	 * 로그인 서비스 메서드
	 */
	public LoginResponseDto login(LoginRequestDto loginRequestDto) {

		//유저 존재하는지 체크
		User user = userRepository.findByName(loginRequestDto.getUserName())
			.orElseThrow(() -> new BusinessException(ExceptionType.NOT_FOUND));

		//비밀번호 검증
		if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
			throw new BusinessException(ExceptionType.INVALID_CREDENTIALS);
		}

		//JWT 발급
		String accessToken = jwtProvider.generateToken(user);

		return new LoginResponseDto(accessToken);
	}

	/**
	 * 관리자 권한 부여 서비스 메서드
	 */
	public GrantAdminRoleResponseDto grantAdminRole(Long userId) {

		//관리자 권한 부여할 유저 조회
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ExceptionType.NOT_FOUND));

		//권한 업데이트
		user.updateRole(UserRole.ADMIN);

		return new GrantAdminRoleResponseDto().toDto(user);
	}


}
