package com.icegame.sysmanage.entity;

/**
 * 用户操作日志表
 * @Package: com.icegame.sysmanage.entity 
 * @author: chsterccw   
 * @date: 2018年7月30日 下午9:24:00
 */
public class Log {
	
	private Long id;
	private Long userId;
	private String userName;
	private String createTime;
	private String operation;
	private String startDate;
	private String endDate;
	private String type;
	private static Log instance;
	
	public static Log getInstance() {
		return instance;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	

}
