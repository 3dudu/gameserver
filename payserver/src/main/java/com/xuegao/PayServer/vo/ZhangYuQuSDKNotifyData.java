package com.xuegao.PayServer.vo;

public class ZhangYuQuSDKNotifyData {
    public String   uid;//	string	是 用户 ID(登录平台时获得 ID)
    public String  role_id;//	string	是	游戏中角色 ID，由充值时传递或角色列表中获取
    public String  gkey;//	number	是 游戏	ID(平台注册获得)
    public String skey;//	number	是 区服	ID(不分区服为 0)
    public String order_id;//	string	是	充值订单 ID
    public String  coins;//	number	是	充值游戏币
    public String moneys;//	number	是	充值金额，单位:分
    public String ext;//	string	否	游戏充值自定义参数，该字符串经过 base64 加密， 需解密后进行解析使用。
    public String time;//	number	是	请求时间戳(可精确到秒)
    public String sign;//	string

    @Override
    public String toString() {
        return "ZhangYuQuSDKNotifyData{" +
                "uid='" + uid + '\'' +
                ", role_id='" + role_id + '\'' +
                ", gkey='" + gkey + '\'' +
                ", skey='" + skey + '\'' +
                ", order_id='" + order_id + '\'' +
                ", coins='" + coins + '\'' +
                ", moneys='" + moneys + '\'' +
                ", ext='" + ext + '\'' +
                ", time='" + time + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
