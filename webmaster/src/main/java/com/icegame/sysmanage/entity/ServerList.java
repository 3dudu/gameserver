package com.icegame.sysmanage.entity;

import java.io.Serializable;

/**
 * 服务器列表
 * @Package: com.icegame.sysmanage.entity
 * @author: chsterccw
 * @date: 2018年7月27日 下午3:10:30
 */
public class ServerList implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Long serverId;
	private String name;
	private String ip;
	private String port;
	private String channel;
	private String group;
	private Long wudaogroup;
	private int close;
	private String beginTime;
	private String updateTime;
	private String passTime;
	private int status;
	private int recommend;
	private String slaveName;

	private Long slaveId;

	private String ver;

	private int slavePort;

	private String nip;

	// 标示是否开服成功
	private int isSuccess;

	// 标示自动和手动
	private int isAuto;

	// 此字段为了区分联运权限，查询的时候需要把 channel带进去
	private String hasChannel;

	// 标示是否是推荐区服
	private int isSuggest;

	public ServerList(){

	}

	public ServerList(int isSuccess, Long serverId){
		this.isSuccess = isSuccess;
		this.serverId = serverId;
	}

	public ServerList(String channel, String beginTime){
		this.channel = channel;
		this.beginTime = beginTime;
	}

	public ServerList(String channel){
		this.channel = channel;
	}

	public Long getServerId() {
		return serverId;
	}
	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}

	public Long getWudaogroup() {
		return wudaogroup;
	}
	public void setWudaogroup(Long wudaogroup) {
		this.wudaogroup = wudaogroup;
	}
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	public int getClose() {
		return close;
	}
	public void setClose(int close) {
		this.close = close;
	}
	public String getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getPassTime() {
		return passTime;
	}
	public void setPassTime(String passTime) {
		this.passTime = passTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getRecommend() {
		return recommend;
	}
	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}
	public Long getSlaveId() {
		return slaveId;
	}
	public void setSlaveId(Long slaveId) {
		this.slaveId = slaveId;
	}
	public int getSlavePort() {
		return slavePort;
	}
	public void setSlavePort(int slavePort) {
		this.slavePort = slavePort;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getSlaveName() {
		return slaveName;
	}

	public void setSlaveName(String slaveName) {
		this.slaveName = slaveName;
	}

	public String getNip() {
		return nip;
	}

	public void setNip(String nip) {
		this.nip = nip;
	}

	public int getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(int isSuccess) {
		this.isSuccess = isSuccess;
	}

	public int getIsAuto() {
		return isAuto;
	}

	public void setIsAuto(int isAuto) {
		this.isAuto = isAuto;
	}

	public String getHasChannel() {
		return hasChannel;
	}

	public void setHasChannel(String hasChannel) {
		this.hasChannel = hasChannel;
	}

	public int getIsSuggest() {
		return isSuggest;
	}

	public void setIsSuggest(int isSuggest) {
		this.isSuggest = isSuggest;
	}
}
