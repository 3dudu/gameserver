package com.icegame.gm.entity;

/**
 * 
 * @author wuzhijian
 *
 */
public class JCsMessage {
	
	private Long id;
	
	private Long msgId;
	
	private Long csId;
	
	private Long thisCsId;
	
	private Long serverId;
	
	private Long playerId;
	
	private String csName;
	
	private String thisCsName;
	
	private String playerName;
	
	private String serverName;
	
	private Long createTime;
	
	private Long updateTime;
	
	private int status;
	
	private int flag;
	
	private String content;
	
	private int isNew;
	
	private int isNewCount;
	
	private Long startTime;
	
	private Long passTime;
	
	private String sign;
	
	private Long banTime;
	
	private Long chattingBanTime;
	
	private Long mailBanTime;
	
	private int vip;
	
	private int operate;
	
	private int shieldCount;
	
	private int usedTitle;
	
	private int skinId;
	
	private Long speakerTypeId;
	
	private int sendMail;

	private int num;

	private String strCreateTime;

	private String strUpdateTime;

	public String getStrCreateTime() {
		return strCreateTime;
	}

	public void setStrCreateTime(String strCreateTime) {
		this.strCreateTime = strCreateTime;
	}

	public String getStrUpdateTime() {
		return strUpdateTime;
	}

	public void setStrUpdateTime(String strUpdateTime) {
		this.strUpdateTime = strUpdateTime;
	}

	public JCsMessage(){
		
	}
	
	public JCsMessage(Long csId){
		this.csId = csId;
	}
	
	public JCsMessage(Long msgId, Long csId, String csName){
		this.msgId = msgId;
		this.csId = csId;
		this.csName = csName;
	}
	
	
	public JCsMessage(Long csId, Long playerId, Long serverId,  String csName, String playerName, String serverName,  Long createTime,Long updateTime,
			String content, int flag,Long startTime,Long passTime) {
		super();
		this.csId = csId;
		this.serverId = serverId;
		this.playerId = playerId;
		this.csName = csName;
		this.playerName = playerName;
		this.serverName = serverName;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.content = content;
		this.flag = flag;
		this.startTime = startTime;
		this.passTime = passTime;
	}
	
	public JCsMessage(Long csId, Long playerId, Long serverId,  String csName, String playerName, String serverName,  Long createTime,
			String content, int flag,int status,Long startTime,Long passTime) {
		super();
		this.csId = csId;
		this.serverId = serverId;
		this.playerId = playerId;
		this.csName = csName;
		this.playerName = playerName;
		this.serverName = serverName;
		this.createTime = createTime;
		this.content = content;
		this.flag = flag;
		this.status = status;
		this.startTime = startTime;
		this.passTime = passTime;
	}
	
	/**
	 * api controller调用，向 jcsmessage插入 一条新的消息
	 * @param playerId
	 * @param serverId
	 * @param playerName
	 * @param serverName
	 * @param createTime
	 * @param content
	 * @param flag
	 */
	public JCsMessage(Long playerId, Long serverId,String playerName, String serverName,Long createTime, Long updateTime,
			String content, int flag,int isNew, Long banTime, Long chattingBanTime, Long mailBanTime, int vip, 
			int usedTitle, int skinId, Long speakerTypeId) {
		super();
		this.serverId = serverId;
		this.playerId = playerId;
		this.playerName = playerName;
		this.serverName = serverName;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.content = content;
		this.flag = flag;
		this.isNew = isNew;
		this.banTime = banTime;
		this.chattingBanTime = chattingBanTime;
		this.mailBanTime = mailBanTime;
		this.vip = vip;
		this.usedTitle = usedTitle;
		this.skinId = skinId;
		this.speakerTypeId = speakerTypeId;
	}
	
	/**
	 * api controller调用，向 jcsmessage_status插入 一条新的消息
	 * @param playerId
	 * @param serverId
	 * @param status
	 */
	public JCsMessage(Long msgId,Long playerId, Long serverId, int status, Long createTime) {
		super();
		this.msgId = msgId;
		this.serverId = serverId;
		this.playerId = playerId;
		this.status = status;
		this.createTime = createTime;
	}
	
	public JCsMessage(Long msgId,Long playerId, Long serverId, Long csId, String csName) {
		super();
		this.msgId = msgId;
		this.serverId = serverId;
		this.playerId = playerId;
		this.csId = csId;
		this.csName = csName;
	}

	public JCsMessage(Long msgId,Long playerId, Long serverId) {
		super();
		this.msgId = msgId;
		this.serverId = serverId;
		this.playerId = playerId;
	}
	
	public JCsMessage(Long playerId, Long serverId,Long banTime,Long chattingBanTime,Long mailBanTime,int vip) {
		super();
		this.serverId = serverId;
		this.playerId = playerId;
		this.banTime = banTime;
		this.chattingBanTime = chattingBanTime;
		this.mailBanTime = mailBanTime;
		this.vip = vip;
	}
	
	public JCsMessage(Long playerId, Long serverId,int vip,int sendMail,Long banTime,Long chattingBanTime,Long mailBanTime) {
		super();
		this.serverId = serverId;
		this.playerId = playerId;
		this.vip = vip;
		this.sendMail = sendMail;
		this.banTime = banTime;
		this.chattingBanTime = chattingBanTime;
		this.mailBanTime = mailBanTime;
	}
	
	public JCsMessage(Long csId, Long serverId, Long playerId, String csName, String playerName,
			String content,Long startTime,Long passTime) {
		super();
		this.csId = csId;
		this.serverId = serverId;
		this.playerId = playerId;
		this.csName = csName;
		this.playerName = playerName;
		this.startTime = startTime;
		this.passTime = passTime;
		this.content = content;
	}
	
	public JCsMessage(Long csId, Long playerId, Long serverId, Long startTime,Long passTime) {
		super();
		this.csId = csId;
		this.playerId = playerId;
		this.serverId = serverId;
		this.startTime = startTime;
		this.passTime = passTime;
	}

	public Long getMsgId() {
		return msgId;
	}

	public void setMsgId(Long msgId) {
		this.msgId = msgId;
	}

	public JCsMessage(Long csId, Long playerId) {
		super();
		this.csId = csId;
		this.playerId = playerId;
	}
	
	public JCsMessage(Long playerId, Long serverId, int operate) {
		super();
		this.playerId = playerId;
		this.serverId = serverId;
		this.operate = operate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCsId() {
		return csId;
	}

	public void setCsId(Long csId) {
		this.csId = csId;
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

	public String getCsName() {
		return csName;
	}

	public void setCsName(String csName) {
		this.csName = csName;
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

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
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

	public int getIsNew() {
		return isNew;
	}

	public void setIsNew(int isNew) {
		this.isNew = isNew;
	}

	public int getIsNewCount() {
		return isNewCount;
	}

	public void setIsNewCount(int isNewCount) {
		this.isNewCount = isNewCount;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Long getThisCsId() {
		return thisCsId;
	}

	public void setThisCsId(Long thisCsId) {
		this.thisCsId = thisCsId;
	}

	public String getThisCsName() {
		return thisCsName;
	}

	public void setThisCsName(String thisCsName) {
		this.thisCsName = thisCsName;
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

	public int getOperate() {
		return operate;
	}

	public void setOperate(int operate) {
		this.operate = operate;
	}

	public int getShieldCount() {
		return shieldCount;
	}

	public void setShieldCount(int shieldCount) {
		this.shieldCount = shieldCount;
	}

	public int getUsedTitle() {
		return usedTitle;
	}

	public void setUsedTitle(int usedTitle) {
		this.usedTitle = usedTitle;
	}

	public int getSkinId() {
		return skinId;
	}

	public void setSkinId(int skinId) {
		this.skinId = skinId;
	}

	public Long getSpeakerTypeId() {
		return speakerTypeId;
	}

	public void setSpeakerTypeId(Long speakerTypeId) {
		this.speakerTypeId = speakerTypeId;
	}

	public int getSendMail() {
		return sendMail;
	}

	public void setSendMail(int sendMail) {
		this.sendMail = sendMail;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
}
