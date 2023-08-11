package com.xuegao.PayServer.vo;

public class YDSDKKuaiWanNotifyData {
    public String  appId; // 商户在支付平台申请的应用 ID
    public String  payNo; // 支付平台的交易订单号
    public String  orderNo; // 商户的订单号
    public String  userName; // 用户名称
    public String   result; // 交易结果 1 表示成功，2 2 表示失败
    public String  pay_channel; // 值由 orderType 决定，orderType=1 支付简码  2 为渠道简码
    public String  amount; // 支付金额（单位元）
    public String  pay_time; // 支付时间，格式 yyyy-MM-dd HH:mm:ss
    public String  userpara;//透传参数，原样回传
    public String  orderType; //订单类型：1 平台订单；2 联运渠道订单
    public String  callback; //是否重复回调：true/false
    public String  sign;

    @Override
    public String toString() {
        return "YDSDKKuaiWanNotifyData{" +
                "appId='" + appId + '\'' +
                ", payNo='" + payNo + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", userName='" + userName + '\'' +
                ", result='" + result + '\'' +
                ", pay_channel='" + pay_channel + '\'' +
                ", amount='" + amount + '\'' +
                ", pay_time='" + pay_time + '\'' +
                ", userpara='" + userpara + '\'' +
                ", orderType='" + orderType + '\'' +
                ", callback='" + callback + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
