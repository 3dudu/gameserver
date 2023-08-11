package com.icegame.sysmanage.vo;

import java.util.Map;

import com.icegame.sysmanage.entity.Group;

public class GroupVo extends Group{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Group group;

	private Map<Long,Long> userIds;
	
	private Map<Long,Long> menuIds;
	
	public Group getGroup() {
		return group;
	}
	public void setGroup(Group group) {
		this.group = group;
	}
	public Map<Long, Long> getUserIds() {
		return userIds;
	}
	public void setUserIds(Map<Long, Long> userIds) {
		this.userIds = userIds;
	}
	public Map<Long, Long> getMenuIds() {
		return menuIds;
	}
	public void setMenuIds(Map<Long, Long> menuIds) {
		this.menuIds = menuIds;
	}
	
	
		
}
