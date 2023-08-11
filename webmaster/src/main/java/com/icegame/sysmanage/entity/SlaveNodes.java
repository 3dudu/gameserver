package com.icegame.sysmanage.entity;

/**
 * slave节点
 * @Package: com.icegame.sysmanage.entity 
 * @author: chsterccw   
 * @date: 2018年8月9日 下午7:41:59
 */
public class SlaveNodes {
	private Long id;
	private String name;
	private String ip;

	public String getNip() {
		return nip;
	}

	public void setNip(String nip) {
		this.nip = nip;
	}

	private String nip;
	private int port;

	private int limit;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
}
