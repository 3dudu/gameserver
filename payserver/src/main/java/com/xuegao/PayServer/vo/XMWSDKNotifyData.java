package com.xuegao.PayServer.vo;



public class XMWSDKNotifyData {
    public String serial;//订单序列号
    public int amount;//金额 单位元
    public String status;//订单状态 unpaid 未支付状态 success 已经支付完成
    public String app_order_id;//订单号
    public String app_user_id;//用户id
    public String sign;//参数签名
    public String app_subject;//应用游戏订单名称
    public String app_ext1;//额外参数 client_id

    @Override
    public String toString() {
        return "XMWSDKNotifyData{" +
                "serial='" + serial + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", app_order_id='" + app_order_id + '\'' +
                ", app_user_id='" + app_user_id + '\'' +
                ", sign='" + sign + '\'' +
                ", app_subject='" + app_subject + '\'' +
                ", app_ext1='" + app_ext1 + '\'' +
                '}';
    }
}
