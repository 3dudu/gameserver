package com.xuegao.PayServer.vo;


/**
 * @Author: LiuBin
 * @Date: 2020/2/18 14:57
 */
public class YDSDKApplePayNotifyData {
    public String appId;        //商户在支付平台申请的应用ID
    public String appVersion;    //应用的版本
    public String orderNo;        //商户的订单号
    public String appleTransactionId;  //苹果交易订单号
    public String payTime;  //支付时间，格式 yyyy-MM-dd HH:mm:ss
    public Integer result;  //交易结果 1 表示成功，2 表示失败
    public Double amount;  //支付金额（单位元）
    public String productNo;  //产品编号 对应苹果的内购点的编号
    public Integer quantity;  //购买产品数量
    public String userName;  //用户名称
    public String userPara;  //透传参数，原样回传
    public String sign;  //签名，具体参看签名算法说明


}
