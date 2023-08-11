package com.xuegao.PayServer.vo;

public class IceBirdSDKNotifyData {

    public String gameId; //游戏ID
    public String channelId;//渠道ID
    public String appId; //游戏包ID
    public String userId; //用户ID
    public String cpOrderId; //游戏方的订单
    public String bfOrderId;//订单ID
    public String channelOrderId;//渠道的订单ID 可能是空字符串
    public int money;//金额 单位分
    public String callbackInfo;//保留字段，暂不可用
    public int orderStatus; //订单状态 0--支付失败  1—支付成功
    public String channelInfo;//渠道自定义信息 string Y 目前不支持，固定为空字符串time 时间戳 string Y
    public String time;
    public String sign; //签名

    @Override
    public String toString() {
        return "IceBirdSDKNotifyData{" +
                "gameId='" + gameId + '\'' +
                ", channelId='" + channelId + '\'' +
                ", appId='" + appId + '\'' +
                ", userId='" + userId + '\'' +
                ", cpOrderId='" + cpOrderId + '\'' +
                ", bfOrderId='" + bfOrderId + '\'' +
                ", channelOrderId='" + channelOrderId + '\'' +
                ", money=" + money +
                ", callbackInfo='" + callbackInfo + '\'' +
                ", orderStatus=" + orderStatus +
                ", channelInfo='" + channelInfo + '\'' +
                ", time='" + time + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
