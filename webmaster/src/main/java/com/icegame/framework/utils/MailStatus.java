package com.icegame.framework.utils;

public enum MailStatus {

	SUCCESS("发送成功", "0"), FAIL("发送失败", "1"), NEW("新加邮件", "2");

	private String name;
	private String status;

	private MailStatus(String name, String status) {
		this.name = name;
		this.status = status;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}