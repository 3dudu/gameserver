package com.xuegao.PayServer.vo;

import java.security.PublicKey;

public class YOUYISDKNotifyData {
    public String cp_order_id;//cp方订单号
    public String order_id;//对方订单号
    public String cp_uid;//用户标识
    public String  goods_id;//商品id
    public String amount;//商品价格 单位元
    public String role_id;//角色id
    public String server_id;//服务器id
    public String extend_params;//透传字段
    public String time;//当前时间戳 十位
    public String  sign;//签名

    @Override
    public String toString() {
        return "YOUYISDKNotifyData{" +
                "cp_order_id='" + cp_order_id + '\'' +
                ", order_id='" + order_id + '\'' +
                ", cp_uid='" + cp_uid + '\'' +
                ", goods_id='" + goods_id + '\'' +
                ", amount='" + amount + '\'' +
                ", role_id='" + role_id + '\'' +
                ", server_id='" + server_id + '\'' +
                ", extend_params='" + extend_params + '\'' +
                ", time='" + time + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
