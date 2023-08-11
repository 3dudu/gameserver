package com.xuegao.PayServer.vo;

public class LeWanSDKNotifyData {
    public String game;	         // 是	游戏唯一标识
    public String cpOrderSn;	 //	是	CP方订单唯一标识
    public String uid;	         //	是	用户唯一标识
    public String amount;        //	是	订单金额，单位：分
    public String trade_state;	 //	是	交易状态，枚举值： SUCCESS：支付成功; REFUND：转入退款; NOTPAY：未支付; CLOSED：已关闭; PAYERROR：支付失败(其他原因，如银行返回失败)
    public String attach;	     //	是	附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用
    public String timeStamp;	 //	是	回调时间戳，单位：s
    public String platform;      //	是	系统类型：ios 或者 android

    @Override
    public String toString() {
        return "LeWanSDKNotifyData{" +
                "game='" + game + '\'' +
                ", cpOrderSn='" + cpOrderSn + '\'' +
                ", uid='" + uid + '\'' +
                ", amount='" + amount + '\'' +
                ", trade_state='" + trade_state + '\'' +
                ", attach='" + attach + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", platform='" + platform + '\'' +
                '}';
    }
}
