package com.xuegao.LoginServer.redisData;

import com.alibaba.fastjson.JSON;
import com.xuegao.LoginServer.po.PfOptionPo;
import com.xuegao.core.datastruct.cache.AbsRedisCacheBeanData;

public class MemPfOptionBean extends AbsRedisCacheBeanData<PfOptionPo> {

	public MemPfOptionBean(String key) {
		super("pf_options"+key, "LoginServer");
	}

	@Override
	protected String format(PfOptionPo t) {
		return JSON.toJSONString(t);
	}

	@Override
	protected PfOptionPo parse(String format) {
		return JSON.parseObject(format, PfOptionPo.class);
	}

}
