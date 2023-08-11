package com.xuegao.PayServer.vo;



public class SamsungNotifyData {
    public String itemId;
    public String paymentId;
    public String orderId;
    public String packageName;
    public String itemName;
    public String purchaseDate;
    public String paymentAmount;
    public String status;
    public String paymentMethod;
    public String mode;
    public String consumeDate;
    public String consumeYN;
    public String consumeDeviceModel;
    public String passThroughParam;
    public String currencyCode;
    public String currencyUnit;

    @Override
    public String toString() {
        return "SamsungNotifyData{" +
                "itemId='" + itemId + '\'' +
                ", paymentId='" + paymentId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", packageName='" + packageName + '\'' +
                ", itemName='" + itemName + '\'' +
                ", purchaseDate='" + purchaseDate + '\'' +
                ", paymentAmount='" + paymentAmount + '\'' +
                ", status='" + status + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", mode='" + mode + '\'' +
                ", consumeDate='" + consumeDate + '\'' +
                ", consumeYN='" + consumeYN + '\'' +
                ", consumeDeviceModel='" + consumeDeviceModel + '\'' +
                ", passThroughParam='" + passThroughParam + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", currencyUnit='" + currencyUnit + '\'' +
                '}';
    }
}
