package com.xuegao.PayServer.vo;

public class Q1SDKNotifyData {
    public int pid;  //渠道id
    public int user; //渠道用户id
    public String order; //游戏订单号
    public String sdkorder; //冰川平台订单号
    public String amount;//金额  单位分
    public int sid; //区服id
    public int actorid; //角色id
    public String CurrencyType; //货币类型
    public String productid; //内购定义商品id
    public byte checkproduct; //是否校验商品id
    public String bankcode;//支付方式
    public String developerPayload; //透传字段
    public String sign; //签名

    @Override
    public String toString() {
        return "Q1SDKNotifyData{" +
                "pid=" + pid +
                ", user=" + user +
                ", order='" + order + '\'' +
                ", sdkorder='" + sdkorder + '\'' +
                ", amount=" + amount +
                ", sid=" + sid +
                ", actorid=" + actorid +
                ", CurrencyType='" + CurrencyType + '\'' +
                ", productid='" + productid + '\'' +
                ", checkproduct=" + checkproduct +
                ", bankcode='" + bankcode + '\'' +
                ", developerPayload='" + developerPayload + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
