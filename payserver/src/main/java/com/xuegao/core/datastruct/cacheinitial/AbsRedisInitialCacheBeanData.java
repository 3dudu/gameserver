package com.xuegao.core.datastruct.cacheinitial;

import com.xuegao.core.datastruct.cache.AbsRedisCacheBeanData;
import com.xuegao.core.datastruct.util.RedisSyncLock;
import com.xuegao.core.exception.DeException;

public abstract class AbsRedisInitialCacheBeanData<T> extends AbsRedisCacheBeanData<T>{
	
	public AbsRedisInitialCacheBeanData(String key,String redisAlias) {
		super(key,redisAlias);
		
	}
	private boolean isinitial=false;
	
	private void initial(){
		if(isinitial){
			return;
		}
		if("none".equals(this.type())){
			RedisSyncLock lock=new RedisSyncLock(key);
			lock.lock();
			try {
				T t=initialData();
				if("none".equals(this.type())){
					if(t==null){
						markBlank();
					}else {
						save(t);
					}
				}
				lock.releaseLock();
			} catch (Exception e) {
				lock.releaseLock();
				logger.error(e.getMessage(),e);
			}
		}
		isinitial=true;
	}
	public abstract T initialData();
	
	
	@Override
	public T get() {
		initial();
		return super.get();
	}

	@Override
	public void save(T t) {
		initial();
		super.save(t);
	}
	
	@Override
	public boolean saveIfNotExist(T t) {
		throw new DeException("it is not possible to be noexist");
	}
	
	private void markBlank(){
		jedisUtil.STRINGS.set(key, "null");
	}
	
	@Override
	public void print() {
		initial();
		super.print();
	}
}
