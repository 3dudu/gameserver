package com.xuegao.core.datastruct.util;

import com.xuegao.core.datastruct.cache.AbsRedisCacheData;

public class SimpleRedisCache extends AbsRedisCacheData<String>{

	public SimpleRedisCache(String key,String redisAlias) {
		super(key,redisAlias);
	}

	@Override
	protected String format(String t) {
		return t;
	}

	@Override
	protected String parse(String format) {
		return format;
	}

	

}
