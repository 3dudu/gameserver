package com.xuegao.LoginServer.po;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.LoginServer.data.DBManager;
import com.xuegao.core.db.po.BasePo;
import com.xuegao.core.db.po.annotation.Column;
import com.xuegao.core.db.po.annotation.Table;

@Table(proxoolAlias = "proxool.GateWay", tableName = "player")
public class PlayerPo extends BasePo {
    @Column
    private long uid;// user表id
    @Column
    private int sid;// server表id
    @Column
    private long pid;// player表id
    @Column
    private int level;// 角色等级
    @Column
    private int area;// 竞技场排名
    @Column
    private int vip;// vip等级
    @Column
    private long money;// 游戏币数量
    @Column
    private long diamond;// 钻石数量
    @Column
    private long battle_power;// 战斗力
    @Column
    private String name;
    @Column
    public String roleStage; //角色关卡
    @Column
    public float roleRechargeAmount; //累充金额
    @Column
    public String serverName; //区服名称
    @Column
    private Date date_create;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public long getDiamond() {
        return diamond;
    }

    public void setDiamond(long diamond) {
        this.diamond = diamond;
    }

    public long getBattle_power() {
        return battle_power;
    }

    public void setBattle_power(long battle_power) {
        this.battle_power = battle_power;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoleStage() {
        return roleStage;
    }

    public void setRoleStage(String roleStage) {
        this.roleStage = roleStage;
    }

    public float getRoleRechargeAmount() {
        return roleRechargeAmount;
    }

    public void setRoleRechargeAmount(float roleRechargeAmount) {
        this.roleRechargeAmount = roleRechargeAmount;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Date getDate_create() {
        return date_create;
    }

    public void setDate_create(Date date_create) {
        this.date_create = date_create;
    }

    public static PlayerPo findByUidAndSid(long uid, int sid) {
        JSONObject object = DBManager.getDB().queryForBean("select * from player where uid=? and sid=?", uid, sid);
        return JSON.toJavaObject(object, PlayerPo.class);
    }

    public static JSONArray findSidByUidAndDate(long uid, Integer limit) {
        JSONArray array = DBManager.getDB()
            .queryForList("select sid,level as lv, name from player where uid=? order by date_create desc limit ?", uid, limit);
        return array;
    }

    public static PlayerPo findByPfUidAndSid(String pf_user ,int sid){
        JSONObject object = DBManager.getDB().queryForBean("select p.* FROM player p JOIN `user` u ON p.uid = u.id where u.pf_user = ? and p.sid = ?", pf_user, sid);
        return JSON.toJavaObject(object, PlayerPo.class);
    }
    public static JSONArray findByPfUid(String pf_user){
        JSONArray array = DBManager.getDB()
                .queryForList("select p.* FROM player p JOIN `user` u ON p.uid = u.id where u.pf_user = ? and u.pf_user !=''", pf_user);
        return array;
    }

    /**
     * 根据uid 与 sid  查询角色相关数据
     * @param uid
     * @param area
     * @return
     */
    public static JSONArray findByUidAndArea(Long uid ,String area) {
        JSONArray array;
        if (uid != null && "-1".equals(area)) {
            array = DBManager.getDB()
                    .queryForList("select * from player where uid = ? ", uid);
        }else {
            array = DBManager.getDB()
                    .queryForList("select * from player where uid = ? and sid = ?", uid, area);
        }
        return array;
    }
}
