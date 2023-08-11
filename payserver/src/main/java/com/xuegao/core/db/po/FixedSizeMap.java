package com.xuegao.core.db.po;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class FixedSizeMap<K,V> {
	private int max_size=1;
	private  LoadingCache<K,V> cache=null;
	
	public FixedSizeMap(int max_size) {
		super();
		this.max_size = max_size;
		this.cache=CacheBuilder
				.newBuilder()
				.maximumSize(this.max_size)
				.expireAfterWrite(24, TimeUnit.HOURS)
				.build(
					new CacheLoader<K, V>() {
						@Override
						public V load(K arg0) throws Exception {
							return null;
						}
	            });
	}

	public void put(K k,V v){
		cache.put(k, v);
	}
	
	public V get(K k){
		return cache.asMap().get(k);
	}
	
	public int size(){
		return cache.asMap().size();
	}
}
