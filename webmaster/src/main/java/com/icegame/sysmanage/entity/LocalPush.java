package com.icegame.sysmanage.entity;

/**
 * 本地推送
 * @author chesterccw
 * @date 2020/5/11
 */
public class LocalPush {

    private Long id;
    private String channel;
    private String channelCode;
    private String pushTime;
    private String context;
    private String hasChannel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPushTime() {
        return pushTime;
    }

    public void setPushTime(String pushTime) {
        this.pushTime = pushTime;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getHasChannel() {
        return hasChannel;
    }

    public void setHasChannel(String hasChannel) {
        this.hasChannel = hasChannel;
    }
}
