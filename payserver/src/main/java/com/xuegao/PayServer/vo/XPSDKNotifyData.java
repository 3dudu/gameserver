package com.xuegao.PayServer.vo;

public class XPSDKNotifyData {
    public String appId;//游戏ID
    public String serverId; //游戏服务器ID
    public String roleId;//角色ID
    public String money;//人民币（分单位）
    public String callbackInfo;//CP透传参数 原厂订单号
    public String openId; //平台订单号
    public String orderId;//参数签名(用于签名比对)
    public int orderStatus;//订单状态(1成功)
    public String amount;//游戏币
    public String remark;//备注
    public String newsign;//验证串
    public String payType;//充值通标识
    public int exchange_rate;//兑换比例


    @Override
    public String toString() {
        return "XPSDKNotifyData{" +
                "appId='" + appId + '\'' +
                ", serverId='" + serverId + '\'' +
                ", roleId='" + roleId + '\'' +
                ", money='" + money + '\'' +
                ", callbackInfo='" + callbackInfo + '\'' +
                ", openId='" + openId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", orderStatus=" + orderStatus +
                ", amount='" + amount + '\'' +
                ", remark='" + remark + '\'' +
                ", newsign='" + newsign + '\'' +
                ", payType='" + payType + '\'' +
                ", exchange_rate=" + exchange_rate +
                '}';
    }
}
