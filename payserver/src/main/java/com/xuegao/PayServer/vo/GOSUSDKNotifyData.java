package com.xuegao.PayServer.vo;

public class GOSUSDKNotifyData {
    public String uid;
    public String  sid;
    public int  amount;
    public String  orderid;
    public int  time;
    public String  charid;
    public String productid;
    public int  count;
    public String extrainfo;
    public String  sign;

    @Override
    public String toString() {
        return "GOSUSDKNotifyData{" +
                "uid='" + uid + '\'' +
                ", sid='" + sid + '\'' +
                ", amount=" + amount +
                ", orderid='" + orderid + '\'' +
                ", time=" + time +
                ", charid='" + charid + '\'' +
                ", productid='" + productid + '\'' +
                ", count=" + count +
                ", extrainfo='" + extrainfo + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
