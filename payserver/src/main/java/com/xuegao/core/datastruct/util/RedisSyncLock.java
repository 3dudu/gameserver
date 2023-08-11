package com.xuegao.core.datastruct.util;


import org.apache.log4j.Logger;

import com.xuegao.core.datastruct.cache.AbsRedisCacheBeanData;

/**
 * 基于redis的缓存锁
 * @author ccx
 */
public class RedisSyncLock {
	public static int period=20;//检索key周期(毫秒)
	public static int timeout=60;//超时(秒)
	static Logger logger=Logger.getLogger(RedisSyncLock.class);
	String lockkey;
	SyncLockBeanData lockData=null;
	
	public RedisSyncLock(String lockkey,String redisAlias) {
		super();
		this.lockkey = lockkey;
		lockData=new SyncLockBeanData(lockkey,redisAlias);
	}
	
	public RedisSyncLock(String redisAlias){
		super();
		StackTraceElement[] elements= Thread.currentThread().getStackTrace();
		StackTraceElement stackTraceElement=elements[elements.length-2];
		this.lockkey=stackTraceElement.getClassName()+"."+stackTraceElement.getMethodName()+"."+stackTraceElement.getLineNumber();
		lockData=new SyncLockBeanData(lockkey,redisAlias);
	}
	public void lock(){
		try {
			while(!lockData.saveIfNotExist(System.currentTimeMillis())){
				Thread.sleep(period);
			}
			lockData.setExpireTime(timeout);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(),e);
		}
	}
	public void releaseLock(){
		lockData.clearKey();
	}
}
class SyncLockBeanData extends AbsRedisCacheBeanData<Long>{

	public SyncLockBeanData(String key,String redisAlias) {
		super("synclock."+key,redisAlias);
	}

	@Override
	protected String format(Long t) {
		if(t==null){
			return "null";
		}
		return String.valueOf(t);
	}

	@Override
	protected Long parse(String format) {
		if(format==null){
			return null;
		}
		return Long.valueOf(format);
	}

}