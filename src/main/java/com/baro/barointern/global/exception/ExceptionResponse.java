package com.baro.barointern.global.exception;


import lombok.Getter;

@Getter
public class ExceptionResponse {
	private final String code;
	private final String message;

	public ExceptionResponse(ExceptionType exceptionType) {
		this.code = exceptionType.name();
		this.message = exceptionType.getErrorMessage();
	}
}
