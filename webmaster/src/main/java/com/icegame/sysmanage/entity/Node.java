package com.icegame.sysmanage.entity;

import java.io.Serializable;

public class Node implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7188112467384640266L;
	
	private Long id;
	private String node;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	
}
