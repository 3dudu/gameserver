package com.xuegao.LoginServer.vo;

public class X7V2DataVo {
    private String roleId;//	String (64)	是	游戏角色ID
    private String guid;//	String (64)	是	小7小号ID
    private String roleName;//	String (100)	是	角色名称
    private String serverId;//	String (64)	是	角色所属区服ID
    private String serverName;//	String (64)	是	角色所属区服名称
    private String roleLevel;//	String (100)	是	角色等级， 示例：100
    private String roleCE;//	String (100)	是	角色战力，示例：20000
    private String roleStage;//	String (100)	是	角色关卡，示例：2-3
    private Float roleRechargeAmount;//	Float (10,2)	是	角色总充值，精度为小数点后2位
    private String roleGuild;//	String (100)	否	角色所属公会

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
        return roleRechargeAmount;
    }
    public void setRoleRechargeAmount(Float roleRechargeAmount) {
        this.roleRechargeAmount = roleRechargeAmount;
    }
    public String getRoleGuild() {
        return roleGuild;
    }
    public void setRoleGuild(String roleGuild) {
        this.roleGuild = roleGuild;
    }

    @Override
    public String toString() {
        return "X7V2DataVo{" +
                "roleId='" + roleId + '\'' +
                ", guid='" + guid + '\'' +
                ", roleName='" + roleName + '\'' +
                ", serverId='" + serverId + '\'' +
                ", serverName='" + serverName + '\'' +
                ", roleLevel='" + roleLevel + '\'' +
                ", roleCE='" + roleCE + '\'' +
                ", roleStage='" + roleStage + '\'' +
                ", roleRechargeAmount=" + roleRechargeAmount +
                ", roleGuild='" + roleGuild + '\'' +
                '}';
    }
}
