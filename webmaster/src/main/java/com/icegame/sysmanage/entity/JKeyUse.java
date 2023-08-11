package com.icegame.sysmanage.entity;



public class JKeyUse {

	private Long id;

	private String cdKey;

	private Long serverId;

	private Long playerId;

	private int useType;

	private String diffType;;

	public JKeyUse(){

	}

	public JKeyUse(Long serverId, Long playerId, String cdKey, int useType, String diffType){
		this.serverId = serverId;
		this.playerId = playerId;
		this.cdKey = cdKey;
		this.useType = useType;
		this.diffType = diffType;
	}

	public JKeyUse(String cdKey){
		this.cdKey = cdKey;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCdKey() {
		return cdKey;
	}

	public void setCdKey(String cdKey) {
		this.cdKey = cdKey;
	}

	public Long getServerId() {
		return serverId;
	}

	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}

	public Long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public int getUseType() {
		return useType;
	}

	public void setUseType(int useType) {
		this.useType = useType;
	}

	public String getDiffType() {
		return diffType;
	}

	public void setDiffType(String diffType) {
		this.diffType = diffType;
	}
}
