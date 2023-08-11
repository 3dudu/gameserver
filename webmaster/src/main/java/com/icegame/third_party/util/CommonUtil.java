package com.icegame.third_party.util;

import com.icegame.framework.utils.StringUtils;
import com.icegame.sysmanage.entity.SlaveNodes;

import java.util.List;

public class CommonUtil {

    public static SlaveNodes randomSlave(List<SlaveNodes> slaveNodesList) {
        return slaveNodesList.isEmpty() ? null : slaveNodesList.get(0);
    }

    public static String slaveUrl(SlaveNodes sn, String path) {
        return "http://" + (StringUtils.isNull(sn.getNip()) ? sn.getIp() : sn.getIp()) + ":" +
                sn.getPort() + path;
    }

    public static String gmUrl(SlaveNodes sn, String path) {
        return "http://" + (StringUtils.isNull(sn.getNip()) ? sn.getIp() : sn.getIp()) + ":" +
                "8081" + path;
    }
}
