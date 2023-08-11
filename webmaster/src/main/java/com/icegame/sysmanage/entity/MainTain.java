package com.icegame.sysmanage.entity;

/**
 * 服务器维护列表
 * @Package: com.icegame.sysmanage.entity 
 * @author: chsterccw   
 * @date: 2018年9月12日 下午4:51:54
 */
public class MainTain {
	
	private Long id;
	private String targets;
	private String title;
	private int closed;
	private String reason;
	private String selectedTargetsList;
	private String targetList;

	private int mainType;
	private String[] channel;
	private String[] slaveNodes;

	private String selectedChannel;
	private String selectedSlave;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTargets() {
		return targets;
	}
	public void setTargets(String targets) {
		this.targets = targets;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getClosed() {
		return closed;
	}
	public void setClosed(int closed) {
		this.closed = closed;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getSelectedTargetsList() {
		return selectedTargetsList;
	}
	public void setSelectedTargetsList(String selectedTargetsList) {
		this.selectedTargetsList = selectedTargetsList;
	}
	public String getTargetList() {
		return targetList;
	}
	public void setTargetList(String targetList) {
		this.targetList = targetList;
	}

	public int getMainType() {
		return mainType;
	}

	public void setMainType(int mainType) {
		this.mainType = mainType;
	}

	public String[] getChannel() {
		return channel;
	}

	public void setChannel(String[] channel) {
		this.channel = channel;
	}

	public String[] getSlaveNodes() {
		return slaveNodes;
	}

	public void setSlaveNodes(String[] slaveNodes) {
		this.slaveNodes = slaveNodes;
	}

	public String getSelectedChannel() {
		return selectedChannel;
	}

	public void setSelectedChannel(String selectedChannel) {
		this.selectedChannel = selectedChannel;
	}

	public String getSelectedSlave() {
		return selectedSlave;
	}

	public void setSelectedSlave(String selectedSlave) {
		this.selectedSlave = selectedSlave;
	}
}
