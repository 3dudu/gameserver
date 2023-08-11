package com.icegame.sysmanage.dto;

import com.icegame.sysmanage.entity.User;

public class UserDto extends User{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long groupId;
	private Long initGroupId;
	private String groupName;

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	
	public Long getInitGroupId() {
		return initGroupId;
	}

	public void setInitGroupId(Long initGroupId) {
		this.initGroupId = initGroupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	
}
