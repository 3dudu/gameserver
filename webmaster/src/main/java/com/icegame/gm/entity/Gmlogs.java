package com.icegame.gm.entity;

public class Gmlogs {
	
	private Long id;
	private String createTime;
	private String type;
	private String loginName;
	private String description;
	
	private String startDate;
	private String endDate;
	
	public Gmlogs() {
		
	}
	
	public Gmlogs(String createTime, String type, String loginName, String description) {
		super();
		this.createTime = createTime;
		this.type = type;
		this.loginName = loginName;
		this.description = description;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
