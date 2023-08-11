package com.xuegao.PayServer.vo;



public class ZTSDKNotifyData {

    public String uid;
    public String money;
    public String time;
    public String sid;
    public String orderid;
    public String ext;
    public String flag;


    @Override
    public String toString() {
        return "ZTSDKNotifyData{" +
                "uid='" + uid + '\'' +
                ", money='" + money + '\'' +
                ", time='" + time + '\'' +
                ", sig='" + sid + '\'' +
                ", orderid='" + orderid + '\'' +
                ", ext='" + ext + '\'' +
                ", flag='" + flag + '\'' +
                '}';
    }
}
