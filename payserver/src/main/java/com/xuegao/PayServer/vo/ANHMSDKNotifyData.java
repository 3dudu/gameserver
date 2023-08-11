package com.xuegao.PayServer.vo;

import java.math.BigDecimal;

public class ANHMSDKNotifyData {

    public String order_num;    //渠道方的订单号
    public String partnerid;    //联运商ID 默认值 newcom
    public String user_id;      //账号ID
    public String gid;          //游戏ID
    public String sid;          //	服务器ID（区服） 透传参数
    public BigDecimal money;    //充值金额（单位 元）
    public String order_ip;     //客户端充值ip
    public int sandbox;         //苹果订单状态，0：正式订单，1：沙盒订单（游戏的测试订单）
    public String game_other;   //透传参数原样返回（建议你做序列化，或者是json_encode字符串，方便你自己用）例如游戏内的用户ID、角色ID等
    public int payts;           //请求时间戳:订单时间
    public String sign;

    @Override
    public String toString() {
        return "ANHMSDKNotifyData{" +
                "order_num='" + order_num + '\'' +
                ", partnerid='" + partnerid + '\'' +
                ", user_id='" + user_id + '\'' +
                ", gid='" + gid + '\'' +
                ", sid='" + sid + '\'' +
                ", money=" + money +
                ", order_ip='" + order_ip + '\'' +
                ", sandbox=" + sandbox +
                ", game_other='" + game_other + '\'' +
                ", payts=" + payts +
                ", sign='" + sign + '\'' +
                '}';
    }
}
