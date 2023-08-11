package com.icegame.third_party.entity;

import org.hibernate.validator.constraints.NotEmpty;

public class XQRequest {

    @NotEmpty(message = "bizParams不能为空")
    private String bizParams;

    @NotEmpty(message = "apiMethod不能为空")
    private String apiMethod;

    @NotEmpty(message = "reqTime不能为空")
    private String reqTime;

    @NotEmpty(message = "appkey不能为空")
    private String appkey;

    @NotEmpty(message = "gameType不能为空")
    private String gameType;

    @NotEmpty(message = "signature不能为空")
    private String signature;

    private String osType;

    public String getBizParams() {
        return bizParams;
    }

    public void setBizParams(String bizParams) {
        this.bizParams = bizParams;
    }

    public String getApiMethod() {
        return apiMethod;
    }

    public void setApiMethod(String apiMethod) {
        this.apiMethod = apiMethod;
    }

    public String getReqTime() {
        return reqTime;
    }

    public void setReqTime(String reqTime) {
        this.reqTime = reqTime;
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

    public XQRequest fromXQShop(XQShop shop) {
        this.setAppkey(shop.getAppkey());
        this.setGameType(shop.getGameType());
        this.setOsType(shop.getOsType());
        return this;
    }

    public void otherAttribute(String reqTime, String apiMethod, String bizParams) {
        this.setReqTime(reqTime);
        this.setApiMethod(apiMethod);
        this.setBizParams(bizParams);
    }

    public String toParams() {
        return "bizParams" + "=" + bizParams + "&" +
                "apiMethod" + "=" + apiMethod + "&" +
                "reqTime" + "=" + reqTime + "&" +
                "appkey" + "=" + appkey + "&" +
                "gameType" + "=" + gameType + "&" +
                "signature" + "=" + signature + "&" +
                "osType" + "=" + osType;
    }
}
