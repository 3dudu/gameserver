package com.xuegao.PayServer.vo;

public class MRSDKNotifyData {

    public String amount;//成功充值金额（元)
    public String callback_info;//(返回给游戏商的私有字段，客户端 extradata 字段传递)
    public String order_id;//订单 ID,合作方订单系统唯一号
    public String role_id;//游戏角色 id
    public String server_id;//游戏服 id
    public int status;//订单状态,1 为成功
    public int timestamp;//时间戳
    public int type;//充值类型。
    public String user_id;//SDK 用户 id
    //public String extendS;//原字段名为extends 扩展参数(此参数不参与签名) 预留
    public String sign;//验证密钥

    @Override
    public String toString() {
        return "MRSDKNotifyData{" +
                "amount='" + amount + '\'' +
                ", callback_info='" + callback_info + '\'' +
                ", order_id='" + order_id + '\'' +
                ", role_id='" + role_id + '\'' +
                ", server_id='" + server_id + '\'' +
                ", status=" + status +
                ", timestamp=" + timestamp +
                ", type=" + type +
                ", user_id='" + user_id + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
