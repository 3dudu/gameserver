package com.icegame.gm.entity;

import java.util.HashMap;
import java.util.Map;

public class ExportTable {
    private int flag;

    private Map<Long,Long> playerIds = new HashMap<Long,Long>();

    private String ids;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public Map<Long, Long> getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(Map<Long, Long> playerIds) {
        this.playerIds = playerIds;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
