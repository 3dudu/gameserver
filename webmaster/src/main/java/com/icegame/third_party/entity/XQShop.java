package com.icegame.third_party.entity;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;

public class XQShop {

    // bizParams 子属性
    @NotEmpty
    private String roleId;

    @NotEmpty
    private String guid;

    @NotEmpty
    private String roleName;

    @NotEmpty
    private String serverId;

    @NotEmpty
    private String serverName;

    @NotEmpty
    private String roleLevel;

    @NotEmpty
    private String roleCE;

    @NotEmpty
    private String roleStage;

    @NotNull
    private Float roleRechargeAmount;

    // 外层属性
    @NotEmpty
    private String appkey;

    @NotEmpty
    private String gameType;

    private String osType;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getRoleLevel() {
        return roleLevel;
    }

    public void setRoleLevel(String roleLevel) {
        this.roleLevel = roleLevel;
    }

    public String getRoleCE() {
        return roleCE;
    }

    public void setRoleCE(String roleCE) {
        this.roleCE = roleCE;
    }

    public String getRoleStage() {
        return roleStage;
    }

    public void setRoleStage(String roleStage) {
        this.roleStage = roleStage;
    }

    public Float getRoleRechargeAmount() {
        DecimalFormat df = new DecimalFormat("#.00");
        return Float.valueOf(df.format(roleRechargeAmount));
    }

    public void setRoleRechargeAmount(Float roleRechargeAmount) {
        this.roleRechargeAmount = roleRechargeAmount;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public void clearExtraAttribute() {
        this.setOsType(null);
        this.setGameType(null);
        this.setAppkey(null);
    }
}
