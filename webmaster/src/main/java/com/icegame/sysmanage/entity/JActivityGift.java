package com.icegame.sysmanage.entity;



public class JActivityGift {

	private Long id;

	private String targets;

	private String title;

	private String channel; //渠道

	private int useType; //使用类型

	private int coin; //金币

	private int dollar; //元宝

	private int merit; //功德

	private String awardStr; //奖励道具

	private int number;

	private boolean useLimit;

	private String selectedTargetsList;

	private String cdKeyList;

	private String diffType; //无限使用   指定区服   指定渠道

	private String sign;

	private Long playerId ;

	private Long serverId ;

	private String cdKey;

	private int type;

	private String platform;

	public JActivityGift(){

	}

	public JActivityGift(String diffType){
		this.diffType = diffType;
	}

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

	public int getUseType() {
		return useType;
	}

	public void setUseType(int useType) {
		this.useType = useType;
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

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public boolean isUseLimit() {
		return useLimit;
	}

	public void setUseLimit(boolean useLimit) {
		this.useLimit = useLimit;
	}

	public String getSelectedTargetsList() {
		return selectedTargetsList;
	}

	public void setSelectedTargetsList(String selectedTargetsList) {
		this.selectedTargetsList = selectedTargetsList;
	}

	public String getCdKeyList() {
		return cdKeyList;
	}

	public void setCdKeyList(String cdKeyList) {
		this.cdKeyList = cdKeyList;
	}

	public String getDiffType() {
		return diffType;
	}

	public void setDiffType(String diffType) {
		this.diffType = diffType;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public Long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public Long getServerId() {
		return serverId;
	}

	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}

	public String getCdKey() {
		return cdKey;
	}

	public void setCdKey(String cdKey) {
		this.cdKey = cdKey;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
}
