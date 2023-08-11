package com.xuegao.PayServer.vo;

/**
 * @Author: LiuBin
 * @Date: 2020/2/3 15:29
 */
public class YDSDKNotifyData {
    public String appId;//商户在支付平台申请的应用 ID
    public String payNo; //支付平台的交易订单号
    public String orderNo;//商户的订单号
    public String userName;//用户名称
    public int result;//交易结果 1 表示成功，2 表示失败
    public String pay_channel; //值由 orderType 决定，orderType=1 时为支 付简码；orderType=2 时为渠道简码。具体 参看附录
    public String amount;//支付金额（单位元）
    public String pay_time;//支付时间，格式 yyyy-MM-dd HH:mm:ss
    public String userpara;//透传参数，原样回传
    public int orderType;//订单类型：1 平台订单；2 联运渠道订单
    public boolean callback;
    public String sign;//签名

    @Override
    public String toString() {
        return "YDSDKNotifyData{" +
                "appId='" + appId + '\'' +
                ", payNo='" + payNo + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", userName='" + userName + '\'' +
                ", result=" + result +
                ", pay_channel='" + pay_channel + '\'' +
                ", amount='" + amount + '\'' +
                ", pay_time='" + pay_time + '\'' +
                ", userpara='" + userpara + '\'' +
                ", orderType=" + orderType +
                ", callback=" + callback +
                ", sign='" + sign + '\'' +
                '}';
    }
}
