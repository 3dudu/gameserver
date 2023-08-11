package com.icegame.sysmanage.dto;

import com.icegame.sysmanage.entity.Group;
import com.icegame.sysmanage.entity.User;

public class GroupDto extends Group{
	
	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
