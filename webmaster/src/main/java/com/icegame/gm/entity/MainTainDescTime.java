package com.icegame.gm.entity;

/**
 * 维护倒计时
 * @Package: com.icegame.gm.entity 
 * @author: chsterccw   
 * @date: 2019年03月25日 下午1:21:04
 */
public class MainTainDescTime {

	private Long id;

	private String expectStartTime;

	// 预计结束时间
	private String expectEndTime;

	private String residueTime;

	public MainTainDescTime() {

	}

	public MainTainDescTime(String expectEndTime) {
		this.expectEndTime = expectEndTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getExpectStartTime() {
		return expectStartTime;
	}

	public void setExpectStartTime(String expectStartTime) {
		this.expectStartTime = expectStartTime;
	}

	public String getExpectEndTime() {
		return expectEndTime;
	}

	public void setExpectEndTime(String expectEndTime) {
		this.expectEndTime = expectEndTime;
	}

	public String getResidueTime() {
		return residueTime;
	}

	public void setResidueTime(String residueTime) {
		this.residueTime = residueTime;
	}
}
