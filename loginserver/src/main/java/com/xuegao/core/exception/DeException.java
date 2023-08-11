package com.xuegao.core.exception;

public class DeException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	private String msg=null;
	private int errorcode=-1;
	
	public DeException(int errorcode,String msg){
		super(msg);
		this.errorcode=errorcode;
		this.msg=msg;
	}
	public DeException(String msg){
		super(msg);
		this.msg=msg;
	}
	public String getMsg() {
		return msg;
	}
	public int getErrorcode() {
		return errorcode;
	}
	
}
