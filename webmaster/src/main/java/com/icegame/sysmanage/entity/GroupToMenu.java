package com.icegame.sysmanage.entity;

import java.io.Serializable;

/**
 * 用户组菜单对应表
 * @Description: TODO
 * @Package com.icegame.sysmanage.entity
 * @author chesterccw
 * @date 2018年1月18日
 */
public class GroupToMenu implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long groupId;
	private Long menuId;
	

	public GroupToMenu() {
		
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public Long getMenuId() {
		return menuId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	
	
}
