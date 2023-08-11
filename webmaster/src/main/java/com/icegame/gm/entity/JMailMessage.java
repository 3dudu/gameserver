package com.icegame.gm.entity;

/**
 * 
 * @author wuzhijian
 *
 */
public class JMailMessage{
	
	private Long id;
	
	private Long createTime;
	
	private Long senderId;
	
	private String senderName;
	
	private Long senderServerId;
	
	private String senderServerName;
	
	private Long receiverId;
	
	private String receiverName;
	
	private Long receiverServerId;
	
	private String receiverServerName;
	
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

	public String getStrCreateTime() {
		return strCreateTime;
	}

	public void setStrCreateTime(String strCreateTime) {
		this.strCreateTime = strCreateTime;
	}

	public JMailMessage(){
		
	}
	
	public JMailMessage(Long senderId, String senderName, Long senderServerId,
			String senderServerName, Long receiverId, String receiverName, Long receiverServerId,
			String receiverServerName, String content,Long startTime,Long passTime) {
		super();
		this.senderId = senderId;
		this.senderName = senderName;
		this.senderServerId = senderServerId;
		this.senderServerName = senderServerName;
		this.receiverId = receiverId;
		this.receiverName = receiverName;
		this.receiverServerId = receiverServerId;
		this.receiverServerName = receiverServerName;
		this.content = content;
		this.startTime = startTime;
		this.passTime = passTime;
	}
	
	
	public JMailMessage(Long createTime, Long senderId, String senderName, Long senderServerId,
			String senderServerName, Long receiverId, String receiverName, Long receiverServerId,
			String receiverServerName, String content) {
		super();
		this.createTime = createTime;
		this.senderId = senderId;
		this.senderName = senderName;
		this.senderServerId = senderServerId;
		this.senderServerName = senderServerName;
		this.receiverId = receiverId;
		this.receiverName = receiverName;
		this.receiverServerId = receiverServerId;
		this.receiverServerName = receiverServerName;
		this.content = content;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public Long getSenderServerId() {
		return senderServerId;
	}

	public void setSenderServerId(Long senderServerId) {
		this.senderServerId = senderServerId;
	}

	public String getSenderServerName() {
		return senderServerName;
	}

	public void setSenderServerName(String senderServerName) {
		this.senderServerName = senderServerName;
	}

	public Long getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public Long getReceiverServerId() {
		return receiverServerId;
	}

	public void setReceiverServerId(Long receiverServerId) {
		this.receiverServerId = receiverServerId;
	}

	public String getReceiverServerName() {
		return receiverServerName;
	}

	public void setReceiverServerName(String receiverServerName) {
		this.receiverServerName = receiverServerName;
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
