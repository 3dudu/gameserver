package com.icegame.sysmanage.vo;

import java.util.Map;

import com.icegame.sysmanage.entity.ServerList;

public class ServerListVo extends ServerList{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<Long,Long> serverIds;
	
	private ServerList serverList;

	public Map<Long, Long> getServerIds() {
		return serverIds;
	}

	public void setServerIds(Map<Long, Long> serverIds) {
		this.serverIds = serverIds;
	}

	public ServerList getServerList() {
		return serverList;
	}

	public void setServerList(ServerList serverList) {
		this.serverList = serverList;
	}
	
	

}
