package com.icegame.framework.utils;

public enum Type {

	ADD("添加", 1),
	DELETE("删除", 2),
	UPDATE("修改", 3),
	LOGIN("登录", 4),
	EXCEL_MAIL_SYNC("发送配置表邮件",5),
	EXCEL_MAIL_UPLOAD("上传邮件配置表",6);

	private String name;
	private int index;

	private Type(String name, int index) {
		this.name = name;
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}