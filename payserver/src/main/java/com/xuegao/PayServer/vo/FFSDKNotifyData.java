package com.xuegao.PayServer.vo;

public class FFSDKNotifyData {
    public String orderid;  //平台订单号
    public String cp_orderid;//订单号
    public String product_id; //商品id
    public String product_type;//商品类型
    public String money;//玩家储值原币金额
    public String currency; //玩家储值原币金额单位
    public String pay_usd;//美金储值金额
    public Integer pay_type; //支付类型
    public Integer all_game_gold; //游戏币
    public Integer game_gold; //真实发送的游戏币
    public Integer extra_game_gold; //额外赠送的游戏币（
    public String uid; //平台账号用户id
    public String role_id; //角色id
    public String role_name; //角色名称
    public Integer server_id; //服务器id
    public Integer pay_date; //支付时间
    public String ext1;
    public String ext2;
    public String ext3;
    public Integer os; //系统
    public String sign; //签名

    @Override
    public String toString() {
        return "FFSDKNotifyData{" +
                "orderid='" + orderid + '\'' +
                ", cp_orderid='" + cp_orderid + '\'' +
                ", product_id='" + product_id + '\'' +
                ", product_type='" + product_type + '\'' +
                ", money='" + money + '\'' +
                ", currency='" + currency + '\'' +
                ", pay_usd='" + pay_usd + '\'' +
                ", pay_type=" + pay_type +
                ", all_game_gold=" + all_game_gold +
                ", game_gold=" + game_gold +
                ", extra_game_gold=" + extra_game_gold +
                ", uid='" + uid + '\'' +
                ", role_id='" + role_id + '\'' +
                ", role_name='" + role_name + '\'' +
                ", server_id=" + server_id +
                ", pay_date=" + pay_date +
                ", ext1='" + ext1 + '\'' +
                ", ext2='" + ext2 + '\'' +
                ", ext3='" + ext3 + '\'' +
                ", os=" + os +
                ", sign='" + sign + '\'' +
                '}';
    }
}
