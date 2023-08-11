package com.xuegao.PayServer.po;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xuegao.PayServer.data.DBManager;
import com.xuegao.core.db.po.BasePo;
import com.xuegao.core.db.po.annotation.Column;
import com.xuegao.core.db.po.annotation.Table;

import java.util.Date;

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
    private int diamond;// 钻石数量
    @Column
    private int battle_power;// 战斗力
    @Column
    private String name;
    @Column
    private Date date_create;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate_create() {
        return date_create;
    }

    public void setDate_create(Date date_create) {
        this.date_create = date_create;
    }

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

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

    public int getBattle_power() {
        return battle_power;
    }

    public void setBattle_power(int battle_power) {
        this.battle_power = battle_power;
    }

    public static PlayerPo findByUidAndSid(long uid, int sid) {
        JSONObject object = DBManager.getDB().queryForBean("select * from player where uid=? and sid=?", uid, sid);
        return JSON.toJavaObject(object, PlayerPo.class);
    }

    public static JSONArray findSidByUidAndDate(long uid, Integer limit) {
        JSONArray array = DBManager.getDB()
            .queryForList("select sid from player where uid=? order by date_create desc limit ?", uid, limit);
        return array;
    }
}
