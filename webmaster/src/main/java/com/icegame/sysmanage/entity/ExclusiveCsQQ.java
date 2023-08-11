package com.icegame.sysmanage.entity;

/**
 * @author wuzhijian
 * @date 2019-05-20
 * 专属客服qq
 */
public class ExclusiveCsQQ {

    private Long id;

    /**
     * 返回客户端的三个数据
     */

    private String money;

    private String isOpen;

    private int times;

    private String qq;

    private Long qqId;

    private Long serverId;

    private Long playerId;

    private String key;

    private String value;

    private String channel;

    public ExclusiveCsQQ(){

    }

    public ExclusiveCsQQ(Long serverId, Long playerId, Long qqId){
        this.serverId = serverId;
        this.playerId = playerId;
        this.qqId = qqId;
    }

    public ExclusiveCsQQ(Long serverId, Long playerId, String channel){
        this.serverId = serverId;
        this.playerId = playerId;
        this.channel = channel;
    }

    public ExclusiveCsQQ(String key, String value){
        this.key = key;
        this.value = value;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public Long getQqId() {
        return qqId;
    }

    public void setQqId(Long qqId) {
        this.qqId = qqId;
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


    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
