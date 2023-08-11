package com.xuegao.PayServer.vo;

public class X7GameSDKNotifyData {
    public String encryp_data;  //RSA的加密数据 格 式 为 ：game_orderid=2018&guid=19663&pay_price=1.00
    public String extends_info_data;//支付的穿透参数
    public String game_area; //游戏区服
    public String game_level;//角色等级
    public String game_orderid;//游戏订单号
    public String game_role_id; //角色号id
    public String game_role_name;//角色名称
    public String sdk_version; //回调的sdk版本
    public String subject; //商品简介
    public String xiao7_goid; //小七服务的订单唯一标识
    public String sign_data;

    @Override
    public String toString() {
        return "X7GameSDKNotifyData{" +
                "encryp_data='" + encryp_data + '\'' +
                ", extends_info_data='" + extends_info_data + '\'' +
                ", game_area='" + game_area + '\'' +
                ", game_level='" + game_level + '\'' +
                ", game_orderid='" + game_orderid + '\'' +
                ", game_role_id='" + game_role_id + '\'' +
                ", game_role_name='" + game_role_name + '\'' +
                ", sdk_version='" + sdk_version + '\'' +
                ", subject='" + subject + '\'' +
                ", xiao7_goid='" + xiao7_goid + '\'' +
                ", sign_data='" + sign_data + '\'' +
                '}';
    }
}
