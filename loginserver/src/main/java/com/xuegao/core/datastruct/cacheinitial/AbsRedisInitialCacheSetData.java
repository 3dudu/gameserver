package com.xuegao.core.datastruct.cacheinitial;

import java.util.HashSet;
import java.util.Set;

import com.xuegao.core.datastruct.cache.AbsRedisCacheSetData;
import com.xuegao.core.datastruct.util.RedisSyncLock;

public abstract class AbsRedisInitialCacheSetData<T> extends AbsRedisCacheSetData<T>{
	
	public AbsRedisInitialCacheSetData(String key,String redisAlias) {
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
				Set<T> list=initialData();
				if("none".equals(this.type())){
					if(list.size()==0){
						markBlank();
					}else {
						for(T t:list){
							super.add(t);
						}
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
	/** 缓存初始化的数据(一般从数据库中获取) */
	public abstract Set<T> initialData();
	
	@Override
	public Set<T> getAll() {
		initial();
		if(isBlank()){
			return new HashSet<T>();
		}
		return super.getAll();
	}

	@Override
	public void add(T t) {
		initial();
		if(isBlank()){
			super.clearKey();
		}
		super.add(t);
	}
	

	@Override
	public void remove(T t) {
		initial();
		if(isBlank()){
			return;
		}
		super.remove(t);
		if(!existKey()){
			markBlank();
		}
	}

	@Override
	public boolean exists(T t) {
		initial();
		if(isBlank()){
			return false;
		}
		return super.exists(t);
	}
	
	@Override
	public long size() {
		initial();
		if(isBlank()){
			return 0;
		}
		return super.size();
	}
	private void markBlank(){
		jedisUtil.STRINGS.set(key, "BLANK_VALUE");
	}
	
	private boolean isBlank(){
		return "string".equals(super.type());
	}
	@Override
	public void print() {
		initial();
		if(isBlank()){
			logger.info("SET:"+key+"==>size:"+0);
		}else {
			super.print();
		}
	}
}
