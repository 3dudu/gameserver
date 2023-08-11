package com.icegame.sysmanage.entity;

import java.io.Serializable;

/**
 * 向登录服、支付服同步状态表
 * @Package: com.icegame.sysmanage.entity 
 * @author: chsterccw   
 * @date: 2018年8月3日 下午3:31:53
 */
public class SyncStatus implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String createTime;
	private String serverName;
	private String serverNodeIp;
	private String status;
	private String type;
	private String startDate;
	private String endDate;
	
	
	
	public SyncStatus(String createTime, String serverName, String serverNodeIp, String type, String status) {
		super();
		this.createTime = createTime;
		this.serverName = serverName;
		this.serverNodeIp = serverNodeIp;
		this.status = status;
		this.type = type;
	}
	
	public SyncStatus() {
		
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getServerNodeIp() {
		return serverNodeIp;
	}
	public void setServerNodeIp(String serverNodeIp) {
		this.serverNodeIp = serverNodeIp;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
}
