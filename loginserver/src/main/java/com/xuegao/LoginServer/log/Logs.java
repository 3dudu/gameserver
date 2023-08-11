package com.xuegao.LoginServer.log;

import com.xuegao.LoginServer.data.Constants.KindLog;

public class Logs {
	private String userId; // 用户id
	private long createTime; // 记录时间
	private String desc; // 备注
	private int kindId;	//日志类型id
	private String kindName; //日志类型名称

	public Logs( KindLog kind) {
		this.createTime = System.currentTimeMillis();
		this.kindId =  kind.getKindId();
		this.kindName =  kind.getKindName();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}


	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getKindId() {
		return kindId;
	}

	public void setKindId(int kindId) {
		this.kindId = kindId;
	}

	public String getKindName() {
		return kindName;
	}

	public void setKindName(String kindName) {
		this.kindName = kindName;
	}

}
