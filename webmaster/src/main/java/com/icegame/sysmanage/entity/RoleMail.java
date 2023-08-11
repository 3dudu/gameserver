package com.icegame.sysmanage.entity;

/**
 * 个人多人邮件
 * @Package: com.icegame.sysmanage.entity 
 * @author: chsterccw   
 * @date: 2018年8月6日 下午2:58:05
 */
public class RoleMail {
	private Long id;
	private Long sid;
	private Long pid;
	private String context;
	private String createTime;
	private String startDate;
	private String endDate;
	private String sidPid;
	private String awardStr;
	private String subject;
	/**
	 * 标识邮件状态{"0":"发送成功","1":"发送失败,等待重新发送","2":"新加邮件,还没有发送"}
	 */
	private String status;

	public RoleMail(){

	}

	public RoleMail(Long id, String status) {
		this.id = id;
		this.status = status;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getSid() {
		return sid;
	}
	public void setSid(Long sid) {
		this.sid = sid;
	}
	public Long getPid() {
		return pid;
	}
	public void setPid(Long pid) {
		this.pid = pid;
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
	public String getSidPid() {
		return sidPid;
	}
	public void setSidPid(String sidPid) {
		this.sidPid = sidPid;
	}

	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getAwardStr() {
		return awardStr;
	}
	public void setAwardStr(String awardStr) {
		this.awardStr = awardStr;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String toString() {
		return "RoleMail [id=" + id + ", sid=" + sid + ", pid=" + pid + ", context=" + context + ", createTime="
				+ createTime + ", startDate=" + startDate + ", endDate=" + endDate + ", sidPid=" + sidPid + "]";
	}
	
}
