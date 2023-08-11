package com.icegame.sysmanage.entity;

/**
 * 帮助系统类型
 * @author wuzhijian
 * @date 2019-03-15 11:47:07
 */
public class HelpSysType {

    private Long id;

    private int diffType;

    private String diffName;

    public HelpSysType(){

    }

    public HelpSysType(String diffName) {
        this.diffName = diffName;
    }

    public HelpSysType(String diffName, int diffType) {
        this.diffName = diffName;
        this.diffType = diffType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDiffType() {
        return diffType;
    }

    public void setDiffType(int diffType) {
        this.diffType = diffType;
    }

    public String getDiffName() {
        return diffName;
    }

    public void setDiffName(String diffName) {
        this.diffName = diffName;
    }
}
