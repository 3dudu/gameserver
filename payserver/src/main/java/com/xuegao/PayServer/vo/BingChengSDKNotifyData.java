package com.xuegao.PayServer.vo;

public class BingChengSDKNotifyData {

    public String   game_id;// | 游戏ID| 023ef77dc5cfe1c0b0381897c1ce2434 | 必传，必定不为空
    public String   uid;// | 支付uid | 10001 | 必传，必定不为空
    public String   amount;// | 支付金额| 23ef77dc568e1c0b0381897c1098jyh | 必传，必定不为空，单位：元 （RMB）
    public String   order_id;// | SDK订单号 | 20191121102776672 | 必传，必定不为空
    public String   cp_order_id;// | 游戏订单号| 15734597671051 | 必传，必定不为空
    public String   role_id;// | 充值角色ID | 1 | 必传，必定不为空
    public String   server_id;// | 充值服务器ID | 1 | 必传，必定不为空
    public String   ext;// | 原样回传参数| 购买一分钱的道具 |
    public String   time;// | 请求时间 | 1574307110 | 必传，必定不为空，10位时间戳，非支付完成时间
    public String   sign;// | 请求签名| 5rgs77dc568e1c0b0381897c1090pouk | 必传，必定不为空,算法见下文

    @Override
    public String toString() {
        return "BingChengSDKNotifyData{" +
                "game_id='" + game_id + '\'' +
                ", uid='" + uid + '\'' +
                ", amount='" + amount + '\'' +
                ", order_id='" + order_id + '\'' +
                ", cp_order_id='" + cp_order_id + '\'' +
                ", role_id='" + role_id + '\'' +
                ", server_id='" + server_id + '\'' +
                ", ext='" + ext + '\'' +
                ", time='" + time + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
