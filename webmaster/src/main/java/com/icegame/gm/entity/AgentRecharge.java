package com.icegame.gm.entity;

/**
 * @author wzy
 * @description 代理充值记录数据类
 * @date 2020/8/4 19:19
 */
public class AgentRecharge {

    private long id;

    private Long serverId;

    private Long playerId;

    private Long userId;

    private Integer proIdx;

    private Double payPrice;

    private String platform;

    private Integer orderType;

    private String channel;

    private String channelCode;

    private String ext;

    private Long createTime;

    private Long finishTime;

    private Integer status;

    private String orderId;

    private String name;

    private String multServerId;

    private String playerName;

    private String moneyType;

    private String loginName;

    public AgentRecharge() {
    }

    public AgentRecharge(String loginName, Long serverId, Long playerId, String playerName, Integer proIdx, Double payPrice, String moneyType,String platform,
                         Integer orderType, String channel, String channelCode, Integer status, String orderId) {
        this.loginName = loginName;
        this.serverId = serverId;
        this.playerId = playerId;
        this.playerName = playerName;
        this.proIdx = proIdx;
        this.payPrice = payPrice;
        this.moneyType = moneyType;
        this.platform = platform;
        this.orderType = orderType;
        this.channel = channel;
        this.channelCode = channelCode;
        this.status = status;
        this.orderId = orderId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getProIdx() {
        return proIdx;
    }

    public void setProIdx(Integer proIdx) {
        this.proIdx = proIdx;
    }

    public Double getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(Double payPrice) {
        this.payPrice = payPrice;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Long finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getMultServerId() {
        return multServerId;
    }

    public void setMultServerId(String multServerId) {
        this.multServerId = multServerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getMoneyType() {
        return moneyType;
    }

    public void setMoneyType(String moneyType) {
        this.moneyType = moneyType;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
}
