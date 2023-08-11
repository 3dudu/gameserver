package com.icegame.gm.entity;

/**
 * 
 * @author wuzhijian
 *
 */
public class JWorldMessage {
	
	private Long id;
	
	private Long serverId;
	
	private Long playerId;
	
	private String playerName;
	
	private String serverName;
	
	private Long createTime;
	
	private String content;
	
	private Long startTime;
	
	private Long passTime;
	
	private String sign;
	
	private Long banTime;
	
	private Long chattingBanTime;
	
	private Long mailBanTime;
	
	private int vip;
	
	private int shieldCount;

	private String strCreateTime;

	public JWorldMessage(){
		
	}

	public String getStrCreateTime() {
		return strCreateTime;
	}

	public void setStrCreateTime(String strCreateTime) {
		this.strCreateTime = strCreateTime;
	}

	public JWorldMessage(Long serverId, Long playerId, String serverName, String playerName, Long createTime,
						 String content) {
		super();
		this.serverId = serverId;
		this.playerId = playerId;
		this.playerName = playerName;
		this.serverName = serverName;
		this.createTime = createTime;
		this.content = content;
	}
	
	public JWorldMessage(Long serverId, Long playerId, String playerName,
			String content,Long startTime,Long passTime) {
		super();
		this.serverId = serverId;
		this.playerId = playerId;
		this.playerName = playerName;
		this.startTime = startTime;
		this.passTime = passTime;
		this.content = content;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public Long getBanTime() {
		return banTime;
	}

	public void setBanTime(Long banTime) {
		this.banTime = banTime;
	}

	public Long getChattingBanTime() {
		return chattingBanTime;
	}

	public void setChattingBanTime(Long chattingBanTime) {
		this.chattingBanTime = chattingBanTime;
	}

	public Long getMailBanTime() {
		return mailBanTime;
	}

	public void setMailBanTime(Long mailBanTime) {
		this.mailBanTime = mailBanTime;
	}

	public int getVip() {
		return vip;
	}

	public void setVip(int vip) {
		this.vip = vip;
	}

	public int getShieldCount() {
		return shieldCount;
	}

	public void setShieldCount(int shieldCount) {
		this.shieldCount = shieldCount;
	}
	
	
	
}
