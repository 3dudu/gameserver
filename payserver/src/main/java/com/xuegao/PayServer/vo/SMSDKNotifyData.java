package com.xuegao.PayServer.vo;

public class SMSDKNotifyData {
    public String orderId;//渠道商订单ID
    public String uid; //渠道商用户唯一标识,登录后的userInfo.getLoginAccount()
    public int amount;//充值金额(单位：元)
    public String coOrderId;//游戏商订单号
    public int serverId;//充值服的ID
    public int success; //订单状态：0成功，1失败
    public String msg;//订单失败时的错误信息
    public String sign;//MD5签名结果

    @Override
    public String toString() {
        return "SMSDKNotifyData{" +
                "orderId='" + orderId + '\'' +
                ", uid='" + uid + '\'' +
                ", amount=" + amount +
                ", coOrderId='" + coOrderId + '\'' +
                ", serverId=" + serverId +
                ", success=" + success +
                ", msg='" + msg + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
