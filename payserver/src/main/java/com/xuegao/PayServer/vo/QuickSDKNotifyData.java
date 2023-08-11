package com.xuegao.PayServer.vo;

public class QuickSDKNotifyData {

    public String is_test;
    public String channel;
    public String channel_uid;
    public String game_order;
    public String order_no;
    public String pay_time;
    public String amount;
    public String status;
    public String extras_params;

    @Override
    public String toString() {
        return "QuickSDKNotifyData{" +
                "is_test='" + is_test + '\'' +
                ", channel='" + channel + '\'' +
                ", channel_uid='" + channel_uid + '\'' +
                ", game_order='" + game_order + '\'' +
                ", order_no='" + order_no + '\'' +
                ", pay_time='" + pay_time + '\'' +
                ", amount='" + amount + '\'' +
                ", status='" + status + '\'' +
                ", extras_params='" + extras_params + '\'' +
                '}';
    }
}
