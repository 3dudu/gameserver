package com.xuegao.PayServer.vo;

public class Game6kwSDKNotifyData {
    public String extension; //拓展字段 创建订单时发送
    public String gameOrder; //游戏订单号
    public String orderID;   //6kw订单号
    public String productID; //商品id
    public String roleID;    //角色id
    public String serverID;  //游戏区服id
    public String  total;    //订单金额 人民币 单位分
    public int uid;          //6kw唯一标识

    @Override
    public String toString() {
        return "{" +
                "extension='" + extension + '\'' +
                ", gameOrder='" + gameOrder + '\'' +
                ", orderID='" + orderID + '\'' +
                ", productID='" + productID + '\'' +
                ", roleID='" + roleID + '\'' +
                ", serverID='" + serverID + '\'' +
                ", total='" + total + '\'' +
                ", uid=" + uid +
                '}';
    }
}
