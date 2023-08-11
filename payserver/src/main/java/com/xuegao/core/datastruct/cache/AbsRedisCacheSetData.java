package com.xuegao.core.datastruct.cache;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.xuegao.core.datastruct.SetData;

public abstract class AbsRedisCacheSetData<T> extends AbsRedisCacheData<T> implements SetData<T>{
	
	public AbsRedisCacheSetData(String key,String redisAlias) {
		super(key,redisAlias);
	}

	@Override
	public Set<T> getAll() {
		Set<String> result = jedisUtil.SETS.smembers(key);
		Set<T> set=new HashSet<T>();
		Iterator<String> iterator=result.iterator();
		while (iterator.hasNext()) {
			String string = (String) iterator.next();
			set.add(parse(string));
		}
		return set;
	}

	@Override
	public void add(T t) {
		jedisUtil.SETS.sadd(key, format(t));
	}
	

	@Override
	public void remove(T t) {
		jedisUtil.SETS.srem(key, format(t));
	}

	@Override
	public boolean exists(T t) {
		return jedisUtil.SETS.sismember(key, format(t));
	}
	
	@Override
	public long size() {
		return jedisUtil.SETS.scard(key);
	}
	
	@Override
	public void print() {
		Set<String> result = jedisUtil.SETS.smembers(key);
		logger.info("SET:"+key+"==>size:"+result.size());
		int i=0;
		for(String string:result){
			logger.info("SET:"+i+++"-->"+string);
		}
	}
}
