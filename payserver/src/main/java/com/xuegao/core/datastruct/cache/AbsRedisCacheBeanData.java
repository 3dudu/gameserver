package com.xuegao.core.datastruct.cache;

import com.xuegao.core.datastruct.BeanData;

public abstract class AbsRedisCacheBeanData<T> extends AbsRedisCacheData<T> implements BeanData<T>{

	public AbsRedisCacheBeanData(String key,String redisAlias) {
		super(key,redisAlias);
	}

	@Override
	public T get() {
		String value=jedisUtil.STRINGS.get(key);
		if("null".equals(value)){
			return null;
		}
		return parse(value);
	}

	@Override
	public void save(T t) {
		if(t==null){
			jedisUtil.STRINGS.set(key, "null");
		}
		jedisUtil.STRINGS.set(key, format(t));
	}
	
	/**
	 * 保存t,如果键存在，保存失败,返回0. 如果键值不存在,返回1；
	 * @param t
	 * @return
	 */
	public boolean saveIfNotExist(T t) {
		long result=jedisUtil.STRINGS.setnx(key, format(t));
		return result==1;
	}
	
	@Override
	public void print() {
		logger.info("BEAN:"+key+"==>"+jedisUtil.STRINGS.get(key));
	}
}
