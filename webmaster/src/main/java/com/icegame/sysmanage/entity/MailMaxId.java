package com.icegame.sysmanage.entity;

/**
 * 用来判断三张邮件表(all_srv_mail、sel_srv_mail、role_mail)
 * 中的最大ID
 * @author chsterccw
 *
 */
public class MailMaxId {
	private Long allSrvMailId;
	private Long srvMailId;
	private Long roleMailId;
	public Long getAllSrvMailId() {
		return allSrvMailId;
	}
	public void setAllSrvMailId(Long allSrvMailId) {
		this.allSrvMailId = allSrvMailId;
	}
	public Long getSrvMailId() {
		return srvMailId;
	}
	public void setSrvMailId(Long srvMailId) {
		this.srvMailId = srvMailId;
	}
	public Long getRoleMailId() {
		return roleMailId;
	}
	public void setRoleMailId(Long roleMailId) {
		this.roleMailId = roleMailId;
	}
	
	
}
