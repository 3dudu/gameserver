package com.icegame.sysmanage.entity;

/**
 * 全服邮件
 * @Package: com.icegame.sysmanage.entity 
 * @author: chsterccw   
 * @date: 2018年8月6日 下午2:58:15
 */
public class AllSrvMail {
	private Long id;
	private String context;
	private String createTime;
	private String startDate;
	private String endDate;
	private String awardStr;
	private String subject;
	/**
	 * 标识邮件状态{"0":"发送成功","1":"发送失败,等待重新发送","2":"新加邮件,还没有发送"}
	 */
	private String status;

	private String channel;

	private String slave;

	private int mailType;

	private String[] selectedChannel;

	private String[] selectedSlave;



	public AllSrvMail(){

	}

	public AllSrvMail(Long id, String status) {
		this.id = id;
		this.status = status;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getAwardStr() {
		return awardStr;
	}
	public void setAwardStr(String awardStr) {
		this.awardStr = awardStr;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getSlave() {
		return slave;
	}

	public void setSlave(String slave) {
		this.slave = slave;
	}

	public int getMailType() {
		return mailType;
	}

	public void setMailType(int mailType) {
		this.mailType = mailType;
	}

	public String[] getSelectedChannel() {
		return selectedChannel;
	}

	public void setSelectedChannel(String[] selectedChannel) {
		this.selectedChannel = selectedChannel;
	}

	public String[] getSelectedSlave() {
		return selectedSlave;
	}

	public void setSelectedSlave(String[] selectedSlave) {
		this.selectedSlave = selectedSlave;
	}
}
