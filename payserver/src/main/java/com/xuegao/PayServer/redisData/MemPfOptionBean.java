package com.xuegao.PayServer.redisData;

import com.alibaba.fastjson.JSON;
import com.xuegao.PayServer.po.PfOptionsPo;
import com.xuegao.core.datastruct.cache.AbsRedisCacheBeanData;

public class MemPfOptionBean extends AbsRedisCacheBeanData<PfOptionsPo> {

	public MemPfOptionBean(String key) {
		super("pf_options"+key, "PayServerRedis");
	}

	@Override
	protected String format(PfOptionsPo t) {
		return JSON.toJSONString(t);
	}

	@Override
	protected PfOptionsPo parse(String format) {
		return JSON.parseObject(format, PfOptionsPo.class);
	}

}
