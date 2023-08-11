package com.icegame.sysmanage.entity;

/**
 * @author wuzhijian
 * @date 2019-08-21
 */
public class JVipqqWxConfig extends JVipBase{

    private Long id;

    private int qqStatus;

    private int qqMoney;

    private int wxStatus;

    private String wxPnum;

    private String channel;


    public JVipqqWxConfig() {

    }

    public JVipqqWxConfig(String channel) {
        this.channel = channel;
    }

    public JVipqqWxConfig(int qqStatus, int qqMoney, int wxStatus, String wxPnum, String channel) {
        this.qqStatus = qqStatus;
        this.qqMoney = qqMoney;
        this.wxStatus = wxStatus;
        this.wxPnum = wxPnum;
        this.channel = channel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQqStatus() {
        return qqStatus;
    }

    public void setQqStatus(int qqStatus) {
        this.qqStatus = qqStatus;
    }

    public int getQqMoney() {
        return qqMoney;
    }

    public void setQqMoney(int qqMoney) {
        this.qqMoney = qqMoney;
    }

    public int getWxStatus() {
        return wxStatus;
    }

    public void setWxStatus(int wxStatus) {
        this.wxStatus = wxStatus;
    }

    public String getWxPnum() {
        return wxPnum;
    }

    public void setWxPnum(String wxPnum) {
        this.wxPnum = wxPnum;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
