package com.icegame.sysmanage.entity;

/**
 * @author wuzhijian
 * @date 2019-05-16
 */
public class AutoOpenServer {

    private Long id;

    private String key;

    private String value;

    private String status;

    private int type;

    private int player;

    private Long serverId;

    private Long maxId;

    private String channel;

    private Long currentTime;


    public AutoOpenServer(){

    }

    public AutoOpenServer(Long serverId){
        this.serverId = serverId;
    }

    public AutoOpenServer(String channel, Long currentTime){
        this.channel = channel;
        this.currentTime = currentTime;
    }

    public AutoOpenServer(String key, String value){
        this.key = key;
        this.value = value;
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getMaxId() {
        return maxId;
    }

    public void setMaxId(Long maxId) {
        this.maxId = maxId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Long currentTime) {
        this.currentTime = currentTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
