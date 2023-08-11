package com.icegame.framework.exception;

public class ServerException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4419666963677603525L;
	
	private ExceptionCode code;

	public ServerException(ExceptionCode code) {
		this.code = code;
	}

	@Override
	public String getMessage() {
		return code.getMessage();
	}

	public String getCode() {
		return code.getErrorCode();
	}

	public void setCode(String code) {
		this.code.setErrorCode(code); 
	}

}
