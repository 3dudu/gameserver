package com.icegame.sysmanage.entity;



public class JActivityGiftKey {

	private Long id;

	private String targets;

	private String title;

	private String channel; //渠道

	private String cdKey; //序列号列表

	private String startTime;

	private String passTime;

	private boolean disabled;

	private int timesLimit;

	private int talkType; //使用类型

	private int coin; //金币

	private int dollar; //元宝

	private int merit; //功德

	private String awardStr; //奖励道具

	private String selectedTargetsList;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTargets() {
		return targets;
	}

	public void setTargets(String targets) {
		this.targets = targets;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getCdKey() {
		return cdKey;
	}

	public void setCdKey(String cdKey) {
		this.cdKey = cdKey;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getPassTime() {
		return passTime;
	}

	public void setPassTime(String passTime) {
		this.passTime = passTime;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public int getTimesLimit() {
		return timesLimit;
	}

	public void setTimesLimit(int timesLimit) {
		this.timesLimit = timesLimit;
	}

	public int getTalkType() {
		return talkType;
	}

	public void setTalkType(int talkType) {
		this.talkType = talkType;
	}

	public int getCoin() {
		return coin;
	}

	public void setCoin(int coin) {
		this.coin = coin;
	}

	public int getDollar() {
		return dollar;
	}

	public void setDollar(int dollar) {
		this.dollar = dollar;
	}

	public int getMerit() {
		return merit;
	}

	public void setMerit(int merit) {
		this.merit = merit;
	}

	public String getAwardStr() {
		return awardStr;
	}

	public void setAwardStr(String awardStr) {
		this.awardStr = awardStr;
	}

	public String getSelectedTargetsList() {
		return selectedTargetsList;
	}

	public void setSelectedTargetsList(String selectedTargetsList) {
		this.selectedTargetsList = selectedTargetsList;
	}
}
