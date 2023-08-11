package com.xuegao.PayServer.vo;

public class Quick7751SDKNotifyData {
    public String uid;//购买道具的用户uid
    public String login_name;//购买道具的用户username
    public String out_order_no;//我们的订单id
    public String order_no;//sdk订单id
    public String pay_time;//下单时间
    public String amount;//支付金额 单位元
    public String status;//0 成功 1 失败
    public String extras_params;//透传字段

    @Override
    public String toString() {
        return "Quick7751SDKNotifyData{" +
                "uid='" + uid + '\'' +
                ", login_name='" + login_name + '\'' +
                ", out_order_no='" + out_order_no + '\'' +
                ", order_no='" + order_no + '\'' +
                ", pay_time='" + pay_time + '\'' +
                ", amount='" + amount + '\'' +
                ", status='" + status + '\'' +
                ", extras_params='" + extras_params + '\'' +
                '}';
    }
}
