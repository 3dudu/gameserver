package com.xuegao.LoginServer.redisData;

import com.alibaba.fastjson.JSON;
import com.xuegao.LoginServer.vo.IdPlatform;
import com.xuegao.core.datastruct.cache.AbsRedisCacheBeanData;

public class MemLoginTokenBean extends AbsRedisCacheBeanData<IdPlatform>{

	public MemLoginTokenBean(String token) {
		super("MemLoginTokenBean_"+token, "LoginServer");
	}

	@Override
	protected String format(IdPlatform t) {
		return ""+JSON.toJSONString(t);
	}

	@Override
	protected IdPlatform parse(String format) {
		return JSON.parseObject(format, IdPlatform.class);
	}

	
	
}
