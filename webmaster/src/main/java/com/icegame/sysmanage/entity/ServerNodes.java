package com.icegame.sysmanage.entity;

import com.icegame.framework.utils.StringUtils;

import java.io.Serializable;

/**
 * 服务器节点
 * @Package: com.icegame.sysmanage.entity
 * @author: chsterccw
 * @date: 2018年8月3日 上午10:09:57
 */
public class ServerNodes implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private String ip;
	private String port;
	private String protocol;
	private String interfaceName;
	private String reInterfaceName;
	private int diff;

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
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getInterfaceName() {
		return interfaceName;
	}
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	public String getReInterfaceName() {
		return reInterfaceName;
	}
	public void setReInterfaceName(String reInterfaceName) {
		this.reInterfaceName = reInterfaceName;
	}
	public int getDiff() {
		return diff;
	}
	public void setDiff(int diff) {
		this.diff = diff;
	}

	public String toUrl(String path) {
		return this.protocol + this.ip + (StringUtils.isNull(this.port) ?
				"" : ":" + this.port) + path;
	}

}
