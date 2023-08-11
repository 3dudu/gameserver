package com.xuegao.LoginServer.log;

public class ElkLog<T> {
    private String kindId;
    private String typeId;
    private Long timestamp;
    private String kindName;
    private String ip;
    private String ept;
    private String appId;
    private String channelCode;
    private String channelTag;
    private String  uid;
    private String playerId;
    private String playerName;
    private String level;
    private String vpl;
    private String dtn;
    private T object;
    public ElkLog(LogConstants.KindLog kind,T object){
        this.kindId=kind.getKindId();
        this.kindName=kind.getKindName();
        this.timestamp=System.currentTimeMillis();
        this.object= object;
    }


    public String getKindId() {
        return kindId;
    }

    public void setKindId(String kindId) {
        this.kindId = kindId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getKindName() {
        return kindName;
    }

    public void setKindName(String kindName) {
        this.kindName = kindName;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getEpt() {
        return ept;
    }

    public void setEpt(String ept) {
        this.ept = ept;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getChannelTag() {
        return channelTag;
    }

    public void setChannelTag(String channelTag) {
        this.channelTag = channelTag;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getVpl() {
        return vpl;
    }

    public void setVpl(String vpl) {
        this.vpl = vpl;
    }

    public String getDtn() {
        return dtn;
    }

    public void setDtn(String dtn) {
        this.dtn = dtn;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
}
