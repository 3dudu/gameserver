package com.icegame.gm.entity;

/**
 * 
 * @author wuzhijian
 *
 */
public class JQuestion {
	
	private Long id;

	private String title;
	
	private int status;
	
	private Long playerId;
	
	private String playerName;
	
	private Long serverId;
	
	private String serverName;
	
	private Long csId;
	
	private String csName;
	
	private Long createTime;
	
	private Long updateTime;
	
	private Long startTime;
	
	private Long passTime;
	
	public JQuestion(){
		
	}
	
	public JQuestion(Long csId,Long startTime,Long passTime,int status,String playerName,Long playerId,Long serverId){
		super();
		this.csId = csId;
		this.startTime = startTime;
		this.passTime = passTime;
		this.status = status;
		this.playerName = playerName;
		this.playerId = playerId;
		this.serverId = serverId;
	}
	
	public JQuestion(Long id,Long csId,String csName){
		super();
		this.id = id;
		this.csId = csId;
		this.csName = csName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public Long getServerId() {
		return serverId;
	}

	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public Long getCsId() {
		return csId;
	}

	public void setCsId(Long csId) {
		this.csId = csId;
	}

	public String getCsName() {
		return csName;
	}

	public void setCsName(String csName) {
		this.csName = csName;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getPassTime() {
		return passTime;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public void setPassTime(Long passTime) {
		this.passTime = passTime;
	}
	
}
