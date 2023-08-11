package com.xuegao.PayServer.redisData;

import com.alibaba.fastjson.JSON;
import com.xuegao.PayServer.po.SlaveServerListPo;
import com.xuegao.core.datastruct.cache.AbsRedisCacheBeanData;

public class MemSlaveServerBean extends AbsRedisCacheBeanData<SlaveServerListPo> {

	/**
	 * 这个key  =  serverId
	 * */
	public MemSlaveServerBean(String key) {
		super("slave_server"+key, "PayServerRedis");
	}

	@Override
	protected String format(SlaveServerListPo t) {
		return JSON.toJSONString(t);
	}

	@Override
	protected SlaveServerListPo parse(String format) {
		return JSON.parseObject(format, SlaveServerListPo.class);
	}

}
