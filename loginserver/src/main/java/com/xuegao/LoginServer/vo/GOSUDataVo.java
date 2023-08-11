package com.xuegao.LoginServer.vo;

public class GOSUDataVo {
    public String  charid;	//int	*		 Id character
    public String  Serverid; //string	*		ServerID of Game
    public String ServerName;	//String	*		Name of server game
    public String  ServerStatus;	//int	*		Status of server game
    public String charname;	//string	*		Name character  (Unicode)
    public String  level;	//int	*		Level
    public String vip;	//int			Vip
    public long gold;	//int

    @Override
    public String toString() {
        return "GOSUDataVo{" +
                "charid=" + charid +
                ", Serverid='" + Serverid + '\'' +
                ", ServerName='" + ServerName + '\'' +
                ", ServerStatus=" + ServerStatus +
                ", charname='" + charname + '\'' +
                ", level=" + level +
                ", vip=" + vip +
                ", gold=" + gold +
                '}';
    }

    public String getCharid() {
        return charid;
    }

    public void setCharid(String charid) {
        this.charid = charid;
    }

    public String getServerid() {
        return Serverid;
    }

    public void setServerid(String serverid) {
        Serverid = serverid;
    }

    public String getServerName() {
        return ServerName;
    }

    public void setServerName(String serverName) {
        ServerName = serverName;
    }

    public String getServerStatus() {
        return ServerStatus;
    }

    public void setServerStatus(String serverStatus) {
        ServerStatus = serverStatus;
    }

    public String getCharname() {
        return charname;
    }

    public void setCharname(String charname) {
        this.charname = charname;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public long getGold() {
        return gold;
    }

    public void setGold(long gold) {
        this.gold = gold;
    }
}
