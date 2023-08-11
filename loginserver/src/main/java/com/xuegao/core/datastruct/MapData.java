package com.xuegao.core.datastruct;

import java.util.List;
import java.util.Map;

public interface MapData<K,V> {
	public void remove(K k);
	public void put(K k,V v);
	public void put(Map<K, V> map);
	
	public boolean exists(K k);
	public V get(K k);
	public Map<K, V> getAll();
	public List<V> values();
	public List<K> keys();
	public long size();
	public List<V> get(K[] ks);
}
