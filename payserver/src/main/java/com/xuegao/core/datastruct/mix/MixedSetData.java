package com.xuegao.core.datastruct.mix;

import java.util.Set;

import com.xuegao.core.datastruct.SetData;
import com.xuegao.core.datastruct.cache.AbsRedisCacheSetData;

public class MixedSetData<T> extends AbsMixedData<AbsRedisCacheSetData<T>,SetData<T>> implements SetData<T>{
	
	public MixedSetData(AbsRedisCacheSetData<T> cacheData, SetData<T> dbData) {
		super(cacheData, dbData);
		checkAndLoad();
	}

	@Override
	public void add(final T t) {
		cacheData.add(t);
		syncExecuteDbOperator(new Runnable() {
			@Override
			public void run() {
				dbData.add(t);
			}
		});
	}

	@Override
	public void remove(final T t) {
		cacheData.remove(t);
		syncExecuteDbOperator(new Runnable() {
			@Override
			public void run() {
				dbData.remove(t);
			}
		});
	}

	@Override
	public Set<T> getAll() {
		try {
			return cacheData.getAll();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return dbData.getAll();
		}
	}

	@Override
	public boolean exists(T t) {
		try {
			return cacheData.exists(t);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return dbData.exists(t);
		}
	}

	@Override
	public long size() {
		try {
			return cacheData.size();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return dbData.size();
		}
	}

	@Override
	public boolean checkDataIsSame() {
		if(cacheData.size()!=dbData.size()){
			return false;
		}
		return true;
	}

	@Override
	public void refreshCacheDataFromDB() {
		Set<T> set=dbData.getAll();
		cacheData.clearKey();
		for(T t:set){
			cacheData.add(t);
		}
	}

}
