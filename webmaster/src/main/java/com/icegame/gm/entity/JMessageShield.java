package com.icegame.gm.entity;

/**
 * 
 * @author wuzhijian
 *
 */
public class JMessageShield  {
	
	private Long msgId;
	
	private String msgContent;
	
	private int times;

	public Long getMsgId() {
		return msgId;
	}

	public void setMsgId(Long msgId) {
		this.msgId = msgId;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

}
