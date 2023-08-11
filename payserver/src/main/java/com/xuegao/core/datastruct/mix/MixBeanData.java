package com.xuegao.core.datastruct.mix;

import com.xuegao.core.datastruct.BeanData;
import com.xuegao.core.datastruct.cache.AbsRedisCacheBeanData;

public class MixBeanData<T> extends AbsMixedData<AbsRedisCacheBeanData<T>,BeanData<T>> implements BeanData<T> {

	public MixBeanData(AbsRedisCacheBeanData<T> cacheData, BeanData<T> dbData) {
		super(cacheData, dbData);
		checkAndLoad();
	}

	@Override
	public void save(final T t) {
		cacheData.save(t);
		syncExecuteDbOperator(new Runnable() {
			@Override
			public void run() {
				dbData.save(t);
			}
		});
	}
	
	@Override
	public T get() {
		try {
			return cacheData.get();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return dbData.get();
		}
		
	}

	@Override
	public boolean checkDataIsSame() {
		T dbT=dbData.get();
		T cacheT=cacheData.get();
		return dbT.equals(cacheT);
	}

	@Override
	public void refreshCacheDataFromDB() {
		T t=dbData.get();
		cacheData.clearKey();
		cacheData.save(t);
	}

	
}
