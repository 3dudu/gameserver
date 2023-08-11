package com.xuegao.core.datastruct.mix;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.xuegao.core.datastruct.cache.AbsCacheData;
import com.xuegao.core.datastruct.util.RedisSyncLock;

public abstract class AbsMixedData<T extends AbsCacheData<?>,E> {
	protected T cacheData;
	protected E dbData;
	private static ExecutorService[] syncExecutors=null;
	protected boolean isCheckedCacheDataLoad=false;
	static Logger logger = Logger.getLogger(AbsMixedData.class);
	
	static{
		int tnum=Runtime.getRuntime().availableProcessors();
		if(tnum<2)tnum=2;
		syncExecutors=new ExecutorService[tnum];
		for(int i=0;i<tnum;i++){
			syncExecutors[i]=Executors.newSingleThreadExecutor();
		}
		logger.info("数据库同步线程:size="+tnum);
	}
	
	public AbsMixedData(T cacheData, E dbData) {
		this.cacheData = cacheData;
		this.dbData = dbData;
	}

	public T getCacheData() {
		return cacheData;
	}

	public E getDbData() {
		return dbData;
	}

	public abstract boolean checkDataIsSame();

	public void checkAndLoad() {
//		if(!isCheckedCacheDataLoad){
//			CacheLoadedMark cacheLoadedMark=new CacheLoadedMark(cacheData.getKey());
//			boolean isloaded=cacheLoadedMark.exists(cacheData.getKey());
//			if(!isloaded){
//				refreshCacheDataFromDB();
//				cacheLoadedMark.add(cacheData.getKey());
//			}
//			isCheckedCacheDataLoad=true;
//		}
	}
	
	public abstract void refreshCacheDataFromDB();
	
	public void syncExecuteDbOperator(final Runnable runnable){
		final String key=cacheData.getKey();
		int index=Math.abs(key.hashCode()%(syncExecutors.length));
		syncExecutors[index].execute(new Runnable() {
			@Override
			public void run() {
				RedisSyncLock lock=new RedisSyncLock(key);
				lock.lock();
				try {
					runnable.run();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}finally{
					lock.releaseLock();
				}
			}
		});
	}
}
