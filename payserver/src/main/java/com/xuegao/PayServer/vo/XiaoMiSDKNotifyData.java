package com.xuegao.PayServer.vo;


public class XiaoMiSDKNotifyData {
    public String appId;//游戏ID
    public String cpOrderId;//开发商订单ID
    public String uid;//用户ID
    public String orderId;//游戏平台订单ID
    public String orderStatus;//订单状态，TRADE_SUCCESS 代表成功
    public String payFee;//支付金额,单位为分,即0.01 米币。
    public String productCode;//商品代码
    public String productName;//商品名称
    public String productCount;//商品数量
    public String payTime;//支付时间,格式 yyyy-MM-dd HH:mm:ss
    public String orderConsumeType;//订单类型：10：普通订单11：直充直消订单 可选
    public String signature;//签名,签名方法见后面说明
    public String cpUserInfo;//开发商透传信息 可选
    public String partnerGiftConsume;//使用游戏券金额 （如果订单使用游戏券则有,long型），如果有则参与签名 可选

    @Override
    public String toString() {
        return "XiaoMiSDKNotifyData{" +
                "appId='" + appId + '\'' +
                ", cpOrderId='" + cpOrderId + '\'' +
                ", uid='" + uid + '\'' +
                ", orderId='" + orderId + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", payFee='" + payFee + '\'' +
                ", productCode='" + productCode + '\'' +
                ", productName='" + productName + '\'' +
                ", productCount='" + productCount + '\'' +
                ", payTime='" + payTime + '\'' +
                ", orderConsumeType='" + orderConsumeType + '\'' +
                ", signature='" + signature + '\'' +
                ", cpUserInfo='" + cpUserInfo + '\'' +
                ", partnerGiftConsume='" + partnerGiftConsume + '\'' +
                '}';
    }
}
