package com.xuegao.PayServer.vo;

public class VIVOSDKNotifyData {
    public String respCode;//响应码
    public String respMsg;//响应信息
    public String signMethod;//签名方法
    public String signature;//签名信息
    public String tradeType;//交易种类
    public String tradeStatus;//交易状态
    public String cpId;//Cp-id
    public String appId;//appId
    public String uid;//uid
    public String cpOrderNumber;//商户自定义的订单号
    public String orderNumber;//交易流水号
    public Long orderAmount;//交易金额 单位：分，币种：人民币，为长整型，如：101，10000
    public String extInfo;//商户透传参数
    public String payTime;//交易时间

    @Override
    public String toString() {
        return "VIVOSDKNotifyData{" +
                "respCode='" + respCode + '\'' +
                ", respMsg='" + respMsg + '\'' +
                ", signMethod='" + signMethod + '\'' +
                ", signature='" + signature + '\'' +
                ", tradeType='" + tradeType + '\'' +
                ", tradeStatus='" + tradeStatus + '\'' +
                ", cpId='" + cpId + '\'' +
                ", appId='" + appId + '\'' +
                ", uid='" + uid + '\'' +
                ", cpOrderNumber='" + cpOrderNumber + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", orderAmount=" + orderAmount +
                ", extInfo='" + extInfo + '\'' +
                ", payTime='" + payTime + '\'' +
                '}';
    }
}
