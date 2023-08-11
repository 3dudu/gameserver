package com.icegame.sysmanage.entity;

import java.io.Serializable;

/**
 * 用户组表实体类
 * 
 * @Description: TODO
 * @Package com.icegame.sysmanage.entity
 * @author chesterccw
 * @date 2018年1月22日
 */
public class Group implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long groupId;
	private String groupName;
	private String hasChannel;
	private String[] channel;

	
	public Group() {
		
	}
	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getHasChannel() {
		return hasChannel;
	}

	public void setHasChannel(String hasChannel) {
		this.hasChannel = hasChannel;
	}

	public String[] getChannel() {
		return channel;
	}

	public void setChannel(String[] channel) {
		this.channel = channel;
	}
}
