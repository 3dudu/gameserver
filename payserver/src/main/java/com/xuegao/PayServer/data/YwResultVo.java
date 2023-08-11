package com.xuegao.PayServer.data;

public class YwResultVo {
	
	public  String content;
	public String msg;
	public int ret;
	
	
	public YwResultVo(String content, String msg, int ret) {
		super();
		this.content = content;
		this.msg = msg;
		this.ret = ret;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int getRet() {
		return ret;
	}
	public void setRet(int ret) {
		this.ret = ret;
	}
	
}
