package com.xuegao.core.datastruct.cacheinitial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xuegao.core.datastruct.cache.AbsRedisCacheMapData;
import com.xuegao.core.datastruct.util.RedisSyncLock;

public abstract class AbsRedisInitialCacheMapData<K, V> extends AbsRedisCacheMapData<K, V> {
	
	public AbsRedisInitialCacheMapData(String key,String redisAlias) {
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
				Map<K, V> map=initialData();
				if("none".equals(this.type())){
					if(map.size()==0){
						markBlank();
					}else {
						super.put(map);
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
	public abstract Map<K, V> initialData();
	@Override
	public void remove(K k) {
		initial();
		if(isBlank()){
			return;
		}
		super.remove(k);
		if(!existKey()){
			markBlank();
		}
	}

	@Override
	public void put(K k, V v) {
		initial();
		if(isBlank()){
			super.clearKey();
		}
		super.put(k,v);
	}

	@Override
	public void put(Map<K, V> map) {
		initial();
		if(isBlank()){
			super.clearKey();
		}
		super.put(map);
	}

	@Override
	public boolean exists(K k) {
		initial();
		if(isBlank()){
			return false;
		}
		return super.exists(k);
	}

	@Override
	public V get(K k) {
		initial();
		if(isBlank()){
			return null;
		}
		return super.get(k);
	}

	@Override
	public Map<K, V> getAll() {
		initial();
		if(isBlank()){
			return new HashMap<K, V>();
		}
		return super.getAll();
	}

	@Override
	public List<V> values() {
		initial();
		if(isBlank()){
			return new ArrayList<V>();
		}
		return super.values();
	}

	@Override
	public List<K> keys() {
		initial();
		if(isBlank()){
			return new ArrayList<K>();
		}
		return super.keys();
	}

	@Override
	public long size() {
		initial();
		if(isBlank()){
			return 0;
		}
		return super.size();
	}

	@Override
	public List<V> get(K[] ks) {
		initial();
		if(isBlank()){
			List<V> list=new ArrayList<V>();
			for(int i=0;i<ks.length;i++){
				list.add(null);
			}
			return list;
		}
		return super.get(ks);
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
			logger.info("MAP:"+key+"==>size:"+0);
		}else {
			super.print();
		}
	}
}
