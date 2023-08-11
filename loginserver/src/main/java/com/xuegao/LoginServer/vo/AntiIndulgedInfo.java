package com.xuegao.LoginServer.vo;

public class AntiIndulgedInfo {

	public AntiIndulgedInfo() {
		super();
	}

	public AntiIndulgedInfo(String si, Integer bt, Long ot, Integer ct, String di, String pi) {
		this.si = si;
		this.bt = bt;
		this.ot = ot;
		this.ct = ct;
		this.di = di;
		this.pi = pi;
	}

	public String si;	//游戏内部会话标识

	public Integer bt;	//游戏用户行为类型 0:下线 1：上线

	public Long ot;	//行为发生时间戳，单位秒

	public Integer ct;	//用户行为数据上报类型 0：已认证通过用户 2：游客用户

	public String di;	//客模式设备标识，由游戏运营单位生成，游客用户下必填

	public String pi;	//已通过实名认证用户的唯一标识，已认证通过用户必填

}