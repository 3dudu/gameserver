package com.icegame.framework.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class DefaultExceptionHandler {
	@ExceptionHandler(ServerException.class)
	@ResponseBody
	ExceptionDto handleException(ServerException e) {
		ExceptionDto dto = new ExceptionDto();
		dto.setErrorCode(e.getCode());
		return dto;
	}

}
