package com.icegame.sysmanage.entity;

/**
 * 开关服情况记录表
 * @Package: com.icegame.sysmanage.entity 
 * @author: chsterccw   
 * @date: 2018年9月12日 下午4:52:12
 */
public class OpclServerStatus {
	
	private Long id;
	private String createTime;
	private String status;
	private String type;
	private String startDate;
	private String endDate;
	private String serverId;
	private String failed;
	private String lastOperate;
	private String hosts;
	

	public OpclServerStatus() {

	}

	public OpclServerStatus( String createTime,String hosts, String status, String type, String serverId,String failed,String lastOperate) {
		super();
		this.createTime = createTime;
		this.hosts = hosts;
		this.status = status;
		this.type = type;
		this.serverId = serverId;
		this.failed = failed;
		this.lastOperate = lastOperate;
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
	public String getHosts() {
		return hosts;
	}

	public void setHosts(String hosts) {
		this.hosts = hosts;
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

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getFailed() {
		return failed;
	}

	public void setFailed(String failed) {
		this.failed = failed;
	}

	public String getLastOperate() {
		return lastOperate;
	}

	public void setLastOperate(String lastOperate) {
		this.lastOperate = lastOperate;
	}

}
