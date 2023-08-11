package com.icegame.sysmanage.entity;

/**
 * @author wuzhijian
 * @date 2019-05-21
 */
public class WechatPub {

    private Long id;

    private String isOpen;

    private String name;

    public WechatPub(){

    }

    public WechatPub(String isOpen, String name){
        this.isOpen = isOpen;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
