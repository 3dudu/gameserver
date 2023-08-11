package com.icegame.sysmanage.entity;

/**
 * 区服邮件
 * @Package: com.icegame.sysmanage.entity 
 * @author: chsterccw   
 * @date: 2018年8月6日 下午2:58:15
 */
public class SelSrvMail {
	private Long id;
	private String sid;
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

	public SelSrvMail(){

	}

	public SelSrvMail(Long id, String status) {
		this.id = id;
		this.status = status;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
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
}
