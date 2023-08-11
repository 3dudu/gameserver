package com.xuegao.core.datastruct.mix;

import java.util.List;
import java.util.Map;

import com.xuegao.core.datastruct.MapData;
import com.xuegao.core.datastruct.cache.AbsRedisCacheMapData;

public class MixedMapData<K,V> extends AbsMixedData<AbsRedisCacheMapData<K, V>,MapData<K, V>> implements MapData<K, V> {

	public MixedMapData(AbsRedisCacheMapData<K, V> cacheData, MapData<K, V> dbData) {
		super(cacheData, dbData);
		checkAndLoad();
	}

	@Override
	public void remove(final K k) {
		cacheData.remove(k);
		syncExecuteDbOperator(new Runnable() {
			@Override
			public void run() {
				dbData.remove(k);
			}
		});
	}

	@Override
	public void put(final K k, final V v) {
		cacheData.put(k, v);
		syncExecuteDbOperator(new Runnable() {
			@Override
			public void run() {
				dbData.put(k, v);
			}
		});
	}

	@Override
	public void put(final Map<K, V> map) {
		cacheData.put(map);
		syncExecuteDbOperator(new Runnable() {
			@Override
			public void run() {
				dbData.put(map);
			}
		});
	}

	@Override
	public boolean exists(K k) {
		try {
			return cacheData.exists(k);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return dbData.exists(k);
		}
	}

	@Override
	public V get(K k) {
		try {
			return cacheData.get(k);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return dbData.get(k);
		}
	}

	@Override
	public Map<K, V> getAll() {
		try {
			return cacheData.getAll();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return dbData.getAll();
		}
	}

	@Override
	public List<V> values() {
		try {
			return cacheData.values();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return dbData.values();
		}
	}

	@Override
	public List<K> keys() {
		try {
			return cacheData.keys();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return dbData.keys();
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
	public List<V> get(K[] ks) {
		try {
			return cacheData.get(ks);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return dbData.get(ks);
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
		Map<K, V> map=dbData.getAll();
		cacheData.clearKey();
		cacheData.put(map);
	}

}
