package com.xuegao.core.datastruct.cache;

import java.util.Set;

import com.xuegao.core.redis.JedisUtil;

public abstract class AbsRedisCacheData<T> extends AbsCacheData<T>{

	protected String redisAlias;
	protected JedisUtil jedisUtil;
	
	public AbsRedisCacheData(String key,String redisAlias) {
		super(key);
		this.redisAlias=redisAlias;
		jedisUtil=JedisUtil.getInstance(this.redisAlias);
	}

	@Override
	public void setExpireTime(int seconds) {
		jedisUtil.KEYS.expired(key, seconds);
	}

	@Override
	public boolean existKey() {
		return jedisUtil.KEYS.exists(key);
	}

	@Override
	public void persist() {
		jedisUtil.KEYS.persist(key);
	}

	@Override
	public String type() {
		return jedisUtil.KEYS.type(key);
	}

	@Override
	public long ttl() {
		return jedisUtil.KEYS.ttl(key);
	}

	@Override
	public void clearKey() {
		jedisUtil.KEYS.del(key);
	}
	
	@Override
	public long nextValue() {
		return jedisUtil.STRINGS.incrBy(key, 1);
	}
	@Override
	public long incrBy(long value) {
		return jedisUtil.STRINGS.incrBy(key, value);
	}
	/** 匹配的键 */
	public Set<String> patten(){
		return jedisUtil.KEYS.keys(key);
	}

	public String getRedisAlias() {
		return redisAlias;
	}

	public JedisUtil getJedisUtil() {
		return jedisUtil;
	}
	
	
}
