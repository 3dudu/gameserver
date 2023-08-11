package com.icegame.sysmanage.entity;

/**
 * 平台配置
 * @Package: com.icegame.sysmanage.entity 
 * @author: chsterccw   
 * @date: 2018年9月12日 下午4:52:32
 */
public class PfOptions {
	private Long id;
	private String key;
	private String value;
	private int close;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getClose() {
		return close;
	}
	public void setClose(int close) {
		this.close = close;
	}
	
	
}
