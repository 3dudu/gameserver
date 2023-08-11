package com.icegame.third_party.entity;

public class XQResponse {

    private String bizResp;
    private String apiMethod;
    private String respTime;
    private String appkey;
    private String gameType;
    private String signature;
    private String osType;

    public String getBizResp() {
        return bizResp;
    }

    public void setBizResp(String bizResp) {
        this.bizResp = bizResp;
    }

    public String getApiMethod() {
        return apiMethod;
    }

    public void setApiMethod(String apiMethod) {
        this.apiMethod = apiMethod;
    }

    public String getRespTime() {
        return respTime;
    }

    public void setRespTime(String respTime) {
        this.respTime = respTime;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public XQResponse fromRequest(XQRequest request) {
        this.osType = request.getOsType();
        this.appkey = request.getAppkey();
        this.gameType = request.getGameType();
        this.apiMethod = request.getApiMethod();
        return this;
    }
}
