package com.icegame.sysmanage.entity;

public class JUser {

    private Long id;

    private String name;

    private String passwd;

    private String createTime;

    private String pfUser;

    private String pf;

    private String mailUser;

    private String bindTime;

    private String orderId;

    private String thirdTradeNo;

    private Long pid;

    private String strId;   //解决数值太大的时候js丢失精度的问题


    public JUser(String orderId,String thirdTradeNo,Long pid,String name){
        this.orderId = orderId;
        this.thirdTradeNo = thirdTradeNo;
        this.pid = pid;
        this.name = name;
    }

    public JUser(){

    }

    public JUser(Long id,String name,String passwd){
        this.id = id;
        this.name = name;
        this.passwd = passwd;
    }

    public JUser(Long id){
        this.id = id;
    }

    public JUser(String name){
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPfUser() {
        return pfUser;
    }

    public void setPfUser(String pfUser) {
        this.pfUser = pfUser;
    }

    public String getMailUser() {
        return mailUser;
    }

    public void setMailUser(String mailUser) {
        this.mailUser = mailUser;
    }

    public String getBindTime() {
        return bindTime;
    }

    public void setBindTime(String bindTime) {
        this.bindTime = bindTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getThirdTradeNo() {
        return thirdTradeNo;
    }

    public void setThirdTradeNo(String thirdTradeNo) {
        this.thirdTradeNo = thirdTradeNo;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getPf() {
        return pf;
    }

    public void setPf(String pf) {
        this.pf = pf;
    }

    public String getStrId() {
        return strId;
    }

    public void setStrId(String strId) {
        this.strId = strId;
    }
}
