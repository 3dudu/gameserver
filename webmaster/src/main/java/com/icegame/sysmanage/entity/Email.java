package com.icegame.sysmanage.entity;

/**
 * @author wuzhijian
 * @date 2019-05-22
 */
public class Email {

    private Long id;

    private String email;

    private int type;

    public Email() {

    }

    public Email(Long id) {
        this.id = id;
    }

    public Email(String email) {
        this.email = email;
    }

    public Email(String email, int type) {
        this.email = email;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
