package com.icegame.sysmanage.entity;

/**
 * 帮助系统记录的标签类型
 * @author wuzhijian
 * @date 2019-03-29 17:50:07
 */
public class HelpSysSign {

    private Long id;

    private int signType;

    private String signName;

    public HelpSysSign(){

    }

    public HelpSysSign(String signName) {
        this.signName = signName;
    }

    public HelpSysSign(String signName, int signType) {
        this.signName = signName;
        this.signType = signType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSignType() {
        return signType;
    }

    public void setSignType(int signType) {
        this.signType = signType;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }
}
