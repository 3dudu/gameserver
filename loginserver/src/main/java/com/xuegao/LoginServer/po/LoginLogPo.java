package com.xuegao.LoginServer.po;

import java.util.Date;

import com.xuegao.core.db.po.BasePo;
import com.xuegao.core.db.po.annotation.Column;
import com.xuegao.core.db.po.annotation.Table;

@Table(proxoolAlias="proxool.GateWay",tableName="login_log")
public class LoginLogPo extends BasePo{
	
	@Column
	private int sid;//区服ID
	@Column
	private int pid;//角色ID
	@Column
	private String channel;//渠道
	@Column
	private String platform;//平台
	@Column
	private Date create_time;//创建时间
	@Column
	private boolean reg;//注册(0为登陆 1为注册
	@Column
	private String create_date;//创建日期
	@Column
	private long uid;//渠道号
	public int getSid() {
		return sid;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public boolean isReg() {
		return reg;
	}
	public void setReg(boolean reg) {
		this.reg = reg;
	}
	public String getCreate_date() {
		return create_date;
	}
	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	
}
