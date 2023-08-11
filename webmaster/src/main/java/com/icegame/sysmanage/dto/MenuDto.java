package com.icegame.sysmanage.dto;

import com.icegame.sysmanage.entity.Menu;

/**
 * 功能菜单表实体类
 * 
 * @Description: TODO
 * @Package com.icegame.sysmanage.entity
 * @author chesterccw
 * @date 2018年1月22日
 */
public class MenuDto extends Menu{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long userId;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	
	
}