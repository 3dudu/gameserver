package com.xuegao.PayServer.vo;

public class BilibiliSDKNotifyData {
    public String id; //订单id
    public String order_no; //B站订单号
    public String out_trade_no; //厂商的订单号
    public String uid; //用户id
    public String username; //用户名
    public String role; //角色名
    public String money; //支付金额  单位：分
    public String pay_money; //实际支付金额 单位：分
    public String game_money; //应用内货币
    public String merchant_id; //商户id
    public String game_id; //游戏id
    public String zone_id; //区服id
    public String product_name; //商品名称
    public String product_desc; //商品描述
    public String pay_time; //订单支付时间
    public String client_ip; //客户端ip
    public String extension_info; //穿透字段  传appId
    public String order_status; //订单状态 1 为完成
    public String sign; //md5后的签名

    @Override
    public String toString() {
        return "BilibiliSDKNotifyData{" +
                "id=" + id +
                ", order_no='" + order_no + '\'' +
                ", out_trade_no='" + out_trade_no + '\'' +
                ", uid=" + uid +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", money=" + money +
                ", pay_money=" + pay_money +
                ", game_money=" + game_money +
                ", merchant_id=" + merchant_id +
                ", game_id=" + game_id +
                ", zone_id=" + zone_id +
                ", product_name='" + product_name + '\'' +
                ", product_desc='" + product_desc + '\'' +
                ", pay_time=" + pay_time +
                ", client_ip='" + client_ip + '\'' +
                ", extension_info='" + extension_info + '\'' +
                ", order_status=" + order_status +
                ", sign='" + sign + '\'' +
                '}';
    }
}
