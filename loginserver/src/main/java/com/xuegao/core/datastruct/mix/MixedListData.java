package com.xuegao.core.datastruct.mix;

import java.util.Collection;
import java.util.List;

import com.xuegao.core.datastruct.ListData;
import com.xuegao.core.datastruct.cache.AbsRedisCacheListData;

public class MixedListData<T> extends AbsMixedData<AbsRedisCacheListData<T>,ListData<T>> implements ListData<T>{
	
	public MixedListData(AbsRedisCacheListData<T> cacheData, ListData<T> dbData) {
		super(cacheData, dbData);
		checkAndLoad();
	}

	@Override
	public long add(final T t) {
		long size=cacheData.add(t);
		syncExecuteDbOperator(new Runnable() {
			@Override
			public void run() {
				dbData.add(t);
			}
		});
		return size;
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
	public void set(final int index, final T t) {
		cacheData.set(index, t);
		syncExecuteDbOperator(new Runnable() {
			@Override
			public void run() {
				dbData.set(index, t);
			}
		});
	}

	@Override
	public void remove(final int index) {
		cacheData.remove(index);
		syncExecuteDbOperator(new Runnable() {
			@Override
			public void run() {
				dbData.remove(index);
			}
		});
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
	public T get(int index) {
		try {
			return cacheData.get(index);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return dbData.get(index);
		}
	}

	@Override
	public List<T> getRange(int startIndex, int size) {
		try {
			return cacheData.getRange(startIndex, size);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return dbData.getRange(startIndex, size);
		}
	}

	@Override
	public List<T> getAll() {
		try {
			return cacheData.getAll();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return dbData.getAll();
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
		List<T> list=dbData.getAll();
		cacheData.clearKey();
		for(T t:list){
			cacheData.add(t);
		}
	}

	@Override
	public void addAll(final Collection<T> collection) {
		cacheData.addAll(collection);
		syncExecuteDbOperator(new Runnable() {
			@Override
			public void run() {
				dbData.addAll(collection);
			}
		});
	}

	@Override
	public void add(int index, T t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public T popFromHead() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T popFromTail() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void trim(int start, int end) {
		// TODO Auto-generated method stub
		
	}
}
