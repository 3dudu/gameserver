package com.xuegao.core.datastruct.cache;

import org.apache.log4j.Logger;

public abstract class AbsCacheData<T> {
	
	protected String key;
	public static Logger logger=Logger.getLogger(AbsCacheData.class);

	public AbsCacheData(String key) {
		this.key = key;
	}
	/**
	 * object-->string
	 */
	protected abstract String format(T t);
	/**
	 * string-->object
	 */
	protected abstract T parse(String format);
	/**
	 * 设置过期时间
	 */
	public abstract void setExpireTime(int seconds);
	/**
	 * 取消过期时间
	 */
	public abstract void persist();
	/**
	 * 返回存储的类型 string|list|set|zset|hash
	 */
	public abstract String type();

	public abstract boolean existKey();
	/**
	 * 查询过期时间(秒)
	 */
	public abstract long ttl();
	/**
	 * 清除缓存key
	 */
	public abstract void clearKey();
	
	public abstract long nextValue();
	/** 打印数据 */
	public void print(){
		logger.info("key:"+key+",type:"+type()+",ttl:"+ttl());
	}
	
	public String getKey() {
		return key;
	}
	/**
	 * 将key对应的value加上指定的值，只有value可以转为数字时该方法才可用
	 */
	public abstract long incrBy(long value);
}
