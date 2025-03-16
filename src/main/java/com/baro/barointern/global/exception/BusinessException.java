package com.baro.barointern.global.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{

	private final ExceptionType exceptionType;

	public BusinessException(ExceptionType exceptionType) {
		super(exceptionType.getErrorMessage());
		this.exceptionType = exceptionType;
	}
}
