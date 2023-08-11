package com.xuegao.PayServer.vo;

public class OneStoreNotifyData {
    public String orderId;
    public String packageName;
    public String productId;
    public long purchaseTime;
    public String developerPayload;
    public String purchaseId;
    public int purchaseState;
    public String signature;
    public int recurringState;
//    public String originPurchaseData;

    @Override
    public String toString() {
        return "OneStoreNotifyData{" +
                "orderId='" + orderId + '\'' +
                ", packageName='" + packageName + '\'' +
                ", productId='" + productId + '\'' +
                ", purchaseTime=" + purchaseTime +
                ", developerPayload='" + developerPayload + '\'' +
                ", purchaseId='" + purchaseId + '\'' +
                ", purchaseState=" + purchaseState +
                ", signature='" + signature + '\'' +
                ", recurringState=" + recurringState +
                '}';
    }
}
