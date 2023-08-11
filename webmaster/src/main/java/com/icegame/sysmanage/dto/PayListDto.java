package com.icegame.sysmanage.dto;

import com.icegame.sysmanage.entity.JPlayer;
import com.icegame.sysmanage.entity.JUser;

public class PayListDto {

    private static final long serialVersionUID = 1L;

    private String strId;

    private String userName;

    private String createTime;

    private String pfUser;

    private String pf;

    private String mailUser;

    private String bindTime;

    private String pid;

    private String playerName;

    public String getStrId() {
        return strId;
    }

    public void setStrId(String strId) {
        this.strId = strId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getPf() {
        return pf;
    }

    public void setPf(String pf) {
        this.pf = pf;
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

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void adapt(JUser user, JPlayer player){
        if (null != user){
            this.strId = user.getStrId() != null ? user.getStrId() : this.strId;
            this.userName = user.getName() != null ? user.getName() : this.userName;
            this.createTime = user.getCreateTime() != null ? user.getCreateTime() : this.createTime;
            this.pfUser = user.getPfUser() != null ? user.getPfUser() : this.pfUser;
            this.pf = user.getPf() != null ? user.getPf() : this.pf;
            this.mailUser = user.getMailUser() != null ? user.getMailUser() :this.mailUser;
            this.bindTime = user.getBindTime() != null ? user.getBindTime() : this.bindTime;
        }
        if (null != player){
            this.pid = player.getPid() != null ? String.valueOf(player.getPid()) : this.pid;
            this.playerName = player.getName() != null ? player.getName() : this.playerName;
        }
    }

}
