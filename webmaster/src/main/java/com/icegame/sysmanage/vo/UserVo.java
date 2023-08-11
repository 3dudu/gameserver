package com.icegame.sysmanage.vo;

import com.icegame.sysmanage.entity.User;

public class UserVo extends User{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private User user;
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
