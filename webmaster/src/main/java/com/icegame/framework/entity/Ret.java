package com.icegame.framework.entity;

public class Ret {
	private int ret;
	private String msg;
	public int getRet() {
		return ret;
	}
	public void setRet(int ret) {
		this.ret = ret;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean success() {
		return ret == 0;
	}

}
