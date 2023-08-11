package com.xuegao.LoginServer.redisData;

import com.alibaba.fastjson.JSON;
import com.xuegao.LoginServer.vo.AntiIndulgedInfo;
import com.xuegao.LoginServer.vo.UserStatu;
import com.xuegao.core.datastruct.cache.AbsRedisCacheListData;

public class MemAntilnduigedInfoList extends AbsRedisCacheListData<AntiIndulgedInfo> {
    public MemAntilnduigedInfoList() {
        super("AntilnduigedInfoList", "LoginServer");
    }

    @Override
    protected String format(AntiIndulgedInfo antiIndulgedInfo) {
        return JSON.toJSONString(antiIndulgedInfo);
    }

    @Override
    protected AntiIndulgedInfo parse(String format) {
        return JSON.parseObject(format, AntiIndulgedInfo.class);
    }


}
