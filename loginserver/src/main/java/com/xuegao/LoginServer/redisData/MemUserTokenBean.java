package com.xuegao.LoginServer.redisData;

import com.alibaba.fastjson.JSON;
import com.xuegao.LoginServer.vo.IdPlatform;
import com.xuegao.LoginServer.vo.UserStatu;
import com.xuegao.core.datastruct.cache.AbsRedisCacheBeanData;

public class MemUserTokenBean extends AbsRedisCacheBeanData<UserStatu>{

	public MemUserTokenBean(String userId) {
		super("MemUserTokenBean_"+userId, "LoginServer");
	}

	@Override
	protected String format(UserStatu t) {
		return ""+ JSON.toJSONString(t);
	}

	@Override
	protected UserStatu parse(String format) {
		return JSON.parseObject(format, UserStatu.class);
	}
}
