package com.icegame.gm.entity;

import java.util.HashMap;
import java.util.Map;

public class BatchDelete {
	
	private Long playerId;
	
	private String serverInfo;
	
	private String playerName;
	
	private Long serverId;
	
	private int type;
	
	Map<Long,Long> npcIds = new HashMap<Long,Long>();
	
	Map<String,String> magicIds = new HashMap<String,String>();
	
	Map<Long,Long> itemIds = new HashMap<Long,Long>();
	
	Map<Long,Long> horseIds = new HashMap<Long,Long>();

	public Long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}


	public String getServerInfo() {
		return serverInfo;
	}

	public void setServerInfo(String serverInfo) {
		this.serverInfo = serverInfo;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public Map<Long, Long> getNpcIds() {
		return npcIds;
	}

	public void setNpcIds(Map<Long, Long> npcIds) {
		this.npcIds = npcIds;
	}

	public Map<String, String> getMagicIds() {
		return magicIds;
	}

	public void setMagicIds(Map<String, String> magicIds) {
		this.magicIds = magicIds;
	}

	public Map<Long, Long> getItemIds() {
		return itemIds;
	}

	public void setItemIds(Map<Long, Long> itemIds) {
		this.itemIds = itemIds;
	}

	public Map<Long, Long> getHorseIds() {
		return horseIds;
	}

	public void setHorseIds(Map<Long, Long> horseIds) {
		this.horseIds = horseIds;
	}

	public Long getServerId() {
		return serverId;
	}

	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
