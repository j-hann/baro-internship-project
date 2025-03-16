package com.baro.barointern.global.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * BusinessException 처리
	 */
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ExceptionResponse> handleBusinessException(BusinessException e) {
		return ResponseEntity
			.status(e.getExceptionType().getHttpStatus())
			.body(new ExceptionResponse(e.getExceptionType()));
	}

	/**
	 * Spring Security 인증 예외 처리
	 * - 로그인 실패
	 */
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ExceptionResponse> handleAuthException(AuthenticationException e) {
		return ResponseEntity
			.status(HttpStatus.UNAUTHORIZED)
			.body(new ExceptionResponse(ExceptionType.INVALID_TOKEN));
	}

	/**
	 * Spring Security 인가 예외 처리
	 * - 권한 부족
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException e) {
		return ResponseEntity
			.status(HttpStatus.FORBIDDEN)
			.body(new ExceptionResponse(ExceptionType.ACCESS_DENIED));
	}

	/**
	 * JWT 관련 예외 처리
	 */
	@ExceptionHandler(JwtException.class)
	public ResponseEntity<ExceptionResponse> handleJwtException(JwtException e) {
		return ResponseEntity
			.status(HttpStatus.UNAUTHORIZED)
			.body(new ExceptionResponse(ExceptionType.INVALID_TOKEN));
	}

	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<ExceptionResponse> handleExpiredJwtException(ExpiredJwtException e) {
		return ResponseEntity
			.status(HttpStatus.UNAUTHORIZED)
			.body(new ExceptionResponse(ExceptionType.INVALID_TOKEN));
	}

	@ExceptionHandler(MalformedJwtException.class)
	public ResponseEntity<ExceptionResponse> handleExpiredJwtException(MalformedJwtException e) {
		return ResponseEntity
			.status(HttpStatus.UNAUTHORIZED)
			.body(new ExceptionResponse(ExceptionType.INVALID_TOKEN));
	}

	/**
	 * 그 외 예외 처리
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> handleGeneralException(Exception e) {
		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(new ExceptionResponse(ExceptionType.INTERNAL_SERVER_ERROR));
	}

}
