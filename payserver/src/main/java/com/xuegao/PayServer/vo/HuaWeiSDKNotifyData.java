package com.xuegao.PayServer.vo;

public class HuaWeiSDKNotifyData {
    public String result;//支付结果 “0”，表示支付成功,“1”，表示退款成功（暂未启用）
    public String userName; //商户名称，开发者注册的公司名
    public String productName;//商品名称
    public String payType;//支付类型
    public String amount;//商品支付金额 (保留2位小数， 例如：20.00) 注：退款通知下，为退款金额，目前仅仅支持全额退款。
    public String orderId;//华为支付平台订单号。
    public String notifyTime;//通知时间。 (自1970年1月1日0时起的毫秒数)
    public String requestId;//商户订单号
    public String orderTime;//下单时间 yyyy-MM-dd hh:mm:ss 仅在sdk中指定了urlver为2时有效。
    public String tradeTime;//交易/退款时间 yyyy-MM-dd hh:mm:ss 仅在sdk中指定了urlver为2时有效
    public String accessMode;//接入方式：0:移动 1:PC-Web 2Mobile-Web 3:TV 仅在sdk中指定了urlver为2时有效
    public String spending;//渠道开销，保留两位小数，单位元
    public String sysReserved;//商户侧保留信息，原样返回商户调用支付sdk 输入的保留信息。未输入时，不返回
    public String signType;//签名类型，不参与签名，默认值为RSA256，表示使用SHA256WithRSA算法
    public String sign;//RSA签名
    public String bankId;

    @Override
    public String toString() {
        return "HuaWeiSDKNotifyData{" +
                "result='" + result + '\'' +
                ", userName='" + userName + '\'' +
                ", productName='" + productName + '\'' +
                ", payType='" + payType + '\'' +
                ", amount='" + amount + '\'' +
                ", orderId='" + orderId + '\'' +
                ", notifyTime='" + notifyTime + '\'' +
                ", requestId='" + requestId + '\'' +
                ", orderTime='" + orderTime + '\'' +
                ", tradeTime='" + tradeTime + '\'' +
                ", accessMode='" + accessMode + '\'' +
                ", spending='" + spending + '\'' +
                ", sysReserved='" + sysReserved + '\'' +
                ", signType='" + signType + '\'' +
                ", sign='" + sign + '\'' +
                ", bankId='" + bankId + '\'' +
                '}';
    }
}
