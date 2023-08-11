package com.icegame.sysmanage.entity;

import java.io.Serializable;

/**
 * 用户用户组对应表实体类
 * 
 * @Description: TODO
 * @Package com.icegame.sysmanage.entity
 * @author chesterccw
 * @date 2018年1月22日
 */
public class UserToGroup implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long userId;
	private Long groupId;

	public UserToGroup() {
		
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

}
