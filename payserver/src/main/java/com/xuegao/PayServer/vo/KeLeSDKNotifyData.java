package com.xuegao.PayServer.vo;

public class KeLeSDKNotifyData {
    public String uid; //第三方用户uid
    public String gid; //第三方游戏id
    public String cid;//第三方渠道id
    public String platform; //第三方平台标识
    public String time; //充值时间
    public String pay_time;//充值成功时间
    public String cpNo;//我们的订单号
    public String order_no;//第三方订单号
    public String amount;//实际金额
    public String money;//充值金额
    public String serverid;//玩家服务器id
    public String roleid;//玩家角色id
    public String goodsId;//商品id
    public String ext;//透传参数
    public String is_test;//是否测试
    public String sign;//充值签名

    @Override
    public String toString() {
        return "KeLeSDKNotifyData{" +
                "uid='" + uid + '\'' +
                ", gid='" + gid + '\'' +
                ", cid='" + cid + '\'' +
                ", platform='" + platform + '\'' +
                ", time='" + time + '\'' +
                ", pay_time='" + pay_time + '\'' +
                ", cpNo='" + cpNo + '\'' +
                ", order_no='" + order_no + '\'' +
                ", amount='" + amount + '\'' +
                ", money='" + money + '\'' +
                ", serverid='" + serverid + '\'' +
                ", roleid='" + roleid + '\'' +
                ", goodsId='" + goodsId + '\'' +
                ", ext='" + ext + '\'' +
                ", is_test='" + is_test + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
