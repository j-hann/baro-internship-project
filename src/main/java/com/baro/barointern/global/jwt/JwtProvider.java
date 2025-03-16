package com.baro.barointern.global.jwt;


import com.baro.barointern.domain.user.entity.User;
import com.baro.barointern.global.auth.UserDetailsServiceImpl;
import com.baro.barointern.global.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

import com.baro.barointern.global.config.JwtConfig;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtProvider {

	private final JwtConfig jwtConfig;
	private final UserDetailsServiceImpl userDetailsService;
	private final Key signingKey;

	public JwtProvider(JwtConfig jwtConfig, UserDetailsServiceImpl userDetailsService) {
		this.jwtConfig = jwtConfig;
		this.userDetailsService = userDetailsService;
		this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfig.getSecretKey()));
	}

	/**
	 * key 객체 한번 생성 후 재사용
	 * - secret 값 Base64로 디코딩 후, HMAC SHA256 키 객체로 변환
	 */
	private Key getSigningKey() {
		return signingKey;
	}

	/**
	 * Access Token 발급
	 * - 로그인 시 호출
	 */
	public String generateToken(User user) {
		String userName = user.getUserName();
		String role = user.getRole().name();

		Date currentDate = new Date();
		Date expireDate = new Date(currentDate.getTime() + jwtConfig.getExpirationTime());

		//Access Token 생성
		return Jwts.builder()
			.setSubject(userName)
			.claim("role", role)
			.setIssuedAt(currentDate)
			.setExpiration(expireDate)
			.signWith(getSigningKey(), SignatureAlgorithm.HS256)
			.compact();
	}

	/**
	 * JWT 검증 메서드
	 * - 토큰이 유효한지 검사
	 */
	public boolean validateToken(String token) {
		//토큰이 없거나 토큰이 만료되었을 경우
		if (!StringUtils.hasText(token) || isTokenExpired(token)) {
			return false;
		}
		try {
			Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token);

			//검증 성공시
			return true;
		} catch (JwtException e) {
			return false;
		}
	}

	/**
	 * JWT Claims 추출
	 * - 검증된 토큰에서만 호출
	 * - Payload 데이터를 반환하는 메서드
	 */
	public Claims extractClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(getSigningKey())
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	/**
	 * JWT 검증 후 인증 객체 생성 메서드
	 * - validateToken로 토큰 검증 후 claims 추출
	 * - 토큰 검증 후 Authentication 객체 반환하여 SecurityContext에 저장
	 */
	public Authentication getAuthentication(String token) {
		Claims claims = extractClaims(token);
		String userName = claims.getSubject();

		UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

		//인증 객체 반환
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	/**
	 * 토큰 만료 여부 확인
	 */
	public boolean isTokenExpired(String token) {
		Claims claims = extractClaims(token);
		Date currentDate = new Date();

		return claims.getExpiration().before(currentDate);
	}

}
