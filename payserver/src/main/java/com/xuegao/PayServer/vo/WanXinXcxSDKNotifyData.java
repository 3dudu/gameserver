package com.xuegao.PayServer.vo;

public class WanXinXcxSDKNotifyData {
    public String app_order_id;  //游戏订单号
    public String app_role_id;  //角色id
    public String is_sandbox; //是否为测试订单  1是测试订单 0正常订单
    public String j_game_id; //该应用的唯一标识符
    public String product_id; //商品id
    public String order_id;// sdk订单号
    public String server_id;// 区服id
    public int total_fee;//金额 (分)
    public String sign; //签名
    public String user_id; //sdk的用户id
    public String time; //时间戳
    public String tp; //穿透字段

    @Override
    public String toString() {
        return "WanXinXcxSDKNotifyData{" +
                "app_order_id='" + app_order_id + '\'' +
                ", app_role_id='" + app_role_id + '\'' +
                ", is_sandbox='" + is_sandbox + '\'' +
                ", j_game_id='" + j_game_id + '\'' +
                ", product_id='" + product_id + '\'' +
                ", order_id='" + order_id + '\'' +
                ", server_id='" + server_id + '\'' +
                ", total_fee=" + total_fee +
                ", sign='" + sign + '\'' +
                ", user_id='" + user_id + '\'' +
                ", time='" + time + '\'' +
                ", tp='" + tp + '\'' +
                '}';
    }
}
