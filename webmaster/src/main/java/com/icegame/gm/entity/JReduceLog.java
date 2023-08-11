package com.icegame.gm.entity;

public class JReduceLog {
	
	private Long id;
	private Long playerId;
	private String playerName;
	private Long serverId;
	private String serverName;
	private Long npcId;
	private String npcName;
	private Long createTime;
	private String strCreateTime;
	private String items;
	private int potential;
	private int evolveLevel;
	private int level;
	private int exp;
	private Long startTime;
	private Long passTime;
	private String sign;
	
	public JReduceLog(){
		
	}
	
	public JReduceLog(Long serverId, Long playerId, String playerName,Long startTime, Long passTime){
		this.serverId = serverId;
		this.playerId = playerId;
		this.playerName = playerName;
		this.startTime = startTime;
		this.passTime = passTime;
	}
	
	public JReduceLog(Long playerId, String playerName, Long serverId, String serverName, Long npcId, String npcName,
			Long createTime, String items, int potential, int evolveLevel, int level, int exp) {
		super();
		this.playerId = playerId;
		this.playerName = playerName;
		this.serverId = serverId;
		this.serverName = serverName;
		this.npcId = npcId;
		this.npcName = npcName;
		this.createTime = createTime;
		this.items = items;
		this.potential = potential;
		this.evolveLevel = evolveLevel;
		this.level = level;
		this.exp = exp;
	}

	public String getStrCreateTime() {
		return strCreateTime;
	}

	public void setStrCreateTime(String strCreateTime) {
		this.strCreateTime = strCreateTime;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public Long getNpcId() {
		return npcId;
	}
	public void setNpcId(Long npcId) {
		this.npcId = npcId;
	}
	public String getNpcName() {
		return npcName;
	}
	public void setNpcName(String npcName) {
		this.npcName = npcName;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	public String getItems() {
		return items;
	}
	public void setItems(String items) {
		this.items = items;
	}
	public int getPotential() {
		return potential;
	}
	public void setPotential(int potential) {
		this.potential = potential;
	}
	public int getEvolveLevel() {
		return evolveLevel;
	}
	public void setEvolveLevel(int evolveLevel) {
		this.evolveLevel = evolveLevel;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}
	public Long getStartTime() {
		return startTime;
	}
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	public Long getPassTime() {
		return passTime;
	}
	public void setPassTime(Long passTime) {
		this.passTime = passTime;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	
	

}
