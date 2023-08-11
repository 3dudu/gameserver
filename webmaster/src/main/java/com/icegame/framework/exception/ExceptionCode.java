package com.icegame.framework.exception;

public enum ExceptionCode {
	
	ERROR_1001("1001", "请求参数为空"),ERROR_1002("1002", "渠道签名不存在"),ERROR_1003("1002", "加密失败"),ERROR_1004("1002", "校验失败"),ERROR_1005("1002", "解密失败");
	
	private String errorCode;
	private String message;

	private ExceptionCode(String errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
