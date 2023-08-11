package com.xuegao.PayServer.vo;

public class FTNNSDKNotifyData {
    public String orderid;
    public int p_type;
    public String uid;
    public int money;
    public int gamemoney;
    public Integer serverid;
    public String mark;
    public int time;
    public String coupon_mark;
    public Integer coupon_money;
    public String sign;

    @Override
    public String toString() {
        return "FTNNSDKNotifyData{" +
                "orderId='" + orderid + '\'' +
                ", p_type='" + p_type + '\'' +
                ", uid='" + uid + '\'' +
                ", money='" + money + '\'' +
                ", gamemoney='" + gamemoney + '\'' +
                ", serverId='" + serverid + '\'' +
                ", mark='" + mark + '\'' +
                ", time='" + time + '\'' +
                ", coupon_mark='" + coupon_mark + '\'' +
                ", coupon_money='" + coupon_money + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
