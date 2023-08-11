package com.xuegao.core.datastruct;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface LinkedMapData<K,V> {
	public void remove(K k);
	public void put(K k,V v);
	public void put(Map<K, V> map);
	
	
	public boolean exists(K k);
	public V get(K k);
	public LinkedHashMap<K, V> getAll();
	public List<V> values();
	public List<K> keys();
	public long size();
	public List<V> get(K[] ks);
}
