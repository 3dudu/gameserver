package com.xuegao.PayServer.vo;

public class YueNanSDKNotifyData {
    public String GameOrderID;
    public String AteOrderID;
    public String ServerID;
    public String PackageID;
    public String Amount;
    public String Time;
    public String ClientOS;
    public String MethodType;
    public String Sign;

    @Override
    public String toString() {
        return "YueNanSDKNotifyData{" +
                "GameOrderID='" + GameOrderID + '\'' +
                ", AteOrderID='" + AteOrderID + '\'' +
                ", ServerID='" + ServerID + '\'' +
                ", PackageID='" + PackageID + '\'' +
                ", Amount='" + Amount + '\'' +
                ", Time='" + Time + '\'' +
                ", ClientOS='" + ClientOS + '\'' +
                ", MethodType='" + MethodType + '\'' +
                ", Sign='" + Sign + '\'' +
                '}';
    }
}
