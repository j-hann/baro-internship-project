package com.baro.barointern.global.exception;

import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	 * todo : 시큐리티 관련 예외 처리
	 */
	

	/**
	 * JWT 관련 예외 처리
	 */
	@ExceptionHandler(JwtException.class)
	public ResponseEntity<ExceptionResponse> handleJwtException(JwtException e) {
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
