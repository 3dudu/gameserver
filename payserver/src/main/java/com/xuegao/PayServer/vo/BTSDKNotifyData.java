package com.xuegao.PayServer.vo;

public class BTSDKNotifyData {
    public String order_sn; //平台订单
    public String good_name;//商品名
    public String game_order_sn;//游戏订单号
    public String uuid;//用户id 登录校验super_user_id
    public String pay_money; //支付金额 单位元
    public String game_id;//游戏id
    public String service_id;//服务器id
    public String service_name;//服务器名称
    public String role_id;
    public String role_name;
    public String time;//时间戳
    public String pay_status;//支付状态
    public String coupon_amount;//代金券
    public String remark;//扩展参数
    public String sign;//签名
    public String is_test;//是否为测试订单

    @Override
    public String toString() {
        return "BTSDKNotifyData{" +
                "order_sn='" + order_sn + '\'' +
                ", good_name='" + good_name + '\'' +
                ", game_order_sn='" + game_order_sn + '\'' +
                ", uuid='" + uuid + '\'' +
                ", pay_money='" + pay_money + '\'' +
                ", game_id='" + game_id + '\'' +
                ", service_id='" + service_id + '\'' +
                ", service_name='" + service_name + '\'' +
                ", role_id='" + role_id + '\'' +
                ", role_name='" + role_name + '\'' +
                ", time='" + time + '\'' +
                ", pay_status='" + pay_status + '\'' +
                ", coupon_amount='" + coupon_amount + '\'' +
                ", remark='" + remark + '\'' +
                ", sign='" + sign + '\'' +
                ", is_test='" + is_test + '\'' +
                '}';
    }
}
