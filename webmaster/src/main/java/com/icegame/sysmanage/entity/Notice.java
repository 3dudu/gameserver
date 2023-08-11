package com.icegame.sysmanage.entity;

/**
 * 公告
 * @Package: com.icegame.sysmanage.entity
 * @author: chsterccw
 * @date: 2018年9月12日 下午4:50:54
 */
public class Notice {

	private Long id;
	private String title;
	private String channel;
	private String context;
	private String contextReview;
	private String titleColor;
	private String contextColor;
	private String contextReviewColor;

	// 此字段为了区分联运权限，查询的时候需要把 channel带进去
	private String hasChannel;
	private int type;
	private int isNew;
	private int isUnfold;
	private int sort;
	private String startTime;

	private String endTime;

	private int openFlag;   //1开启 0未开启

	public int getOpenFlag() {
		return openFlag;
	}

	public void setOpenFlag(int openFlag) {
		this.openFlag = openFlag;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getContextReview() {
		return contextReview;
	}
	public void setContextReview(String contextReview) {
		this.contextReview = contextReview;
	}

	public String getHasChannel() {
		return hasChannel;
	}

	public void setHasChannel(String hasChannel) {
		this.hasChannel = hasChannel;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getIsNew() {
		return isNew;
	}

	public void setIsNew(int isNew) {
		this.isNew = isNew;
	}

	public int getIsUnfold() {
		return isUnfold;
	}

	public void setIsUnfold(int isUnfold) {
		this.isUnfold = isUnfold;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public String getTitleColor() {
		return titleColor;
	}

	public void setTitleColor(String titleColor) {
		this.titleColor = titleColor;
	}

	public String getContextColor() {
		return contextColor;
	}

	public void setContextColor(String contextColor) {
		this.contextColor = contextColor;
	}

	public String getContextReviewColor() {
		return contextReviewColor;
	}

	public void setContextReviewColor(String contextReviewColor) {
		this.contextReviewColor = contextReviewColor;
	}
}
