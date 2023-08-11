package com.xuegao.PayServer.vo;

public class OPPOSDKNotifyData {

    public String notifyId;//回调通知 ID（该值使用系统为这次支付生成
    public String partnerOrder;//开发者订单号（客户端上传）
    public String productName;//商品名称（客户端上传）
    public String productDesc;//商品描述（客户端上传）
    public int price;//商品价格(以分为单位)
    public int count;//商品数量（一般为 1）
    public String attach; //请求支付时上传的附加参数（客户端上传）
    public String sign; //签名

    @Override
    public String toString() {
        return "OPPOSDKNotifyData{" +
                "notifyId='" + notifyId + '\'' +
                ", partnerOrder='" + partnerOrder + '\'' +
                ", productName='" + productName + '\'' +
                ", productDesc='" + productDesc + '\'' +
                ", price=" + price +
                ", count=" + count +
                ", attach='" + attach + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }


}
