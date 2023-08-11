package com.icegame.sysmanage.vo;

import java.util.Map;

import com.icegame.sysmanage.entity.ServerNodes;

public class ServerNodesVo {
	
	private ServerNodes serverNodes;
	private Map<Long,Long> nodesIds;
	public ServerNodes getServerNodes() {
		return serverNodes;
	}
	public void setServerNodes(ServerNodes serverNodes) {
		this.serverNodes = serverNodes;
	}
	public Map<Long, Long> getNodesIds() {
		return nodesIds;
	}
	public void setNodesIds(Map<Long, Long> nodesIds) {
		this.nodesIds = nodesIds;
	}
	
	

}
