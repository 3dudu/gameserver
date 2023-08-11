package com.xuegao.core.datastruct.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xuegao.core.datastruct.MapData;

public abstract class AbsRedisCacheMapData<K, V> extends AbsRedisCacheData<V> implements MapData<K, V> {

	public AbsRedisCacheMapData(String key,String redisAlias) {
		super(key,redisAlias);
	}

	protected abstract String formatKey(K k);
	
	protected abstract K parseKey(String k);

	@Override
	public void remove(K k) {
		jedisUtil.HASH.hdel(key, formatKey(k));
	}

	@Override
	public void put(K k, V v) {
		jedisUtil.HASH.hset(key, formatKey(k), format(v));
	}

	@Override
	public void put(Map<K, V> map) {
		if(map==null||map.size()==0){
			return;
		}
		Map<String, String> newmap=new HashMap<String, String>();
		for(K k:map.keySet()){
			newmap.put(formatKey(k), format(map.get(k)));
		}
		jedisUtil.HASH.hmset(key, newmap);
	}

	@Override
	public boolean exists(K k) {
		return jedisUtil.HASH.hexists(key, formatKey(k));
	}

	@Override
	public V get(K k) {
		String value=jedisUtil.HASH.hget(key, formatKey(k));
		return parse(value);
	}

	@Override
	public Map<K, V> getAll() {
		Map<String, String> map=jedisUtil.HASH.hgetAll(key);
		Map<K, V> newMap=new HashMap<K, V>();
		for(String key:map.keySet()){
			newMap.put(parseKey(key), parse(map.get(key)));
		}
		return newMap;
	}

	@Override
	public List<V> values() {
		List<String> list=jedisUtil.HASH.hvals(key);
		List<V> list2=new ArrayList<V>();
		for(String s:list){
			list2.add(parse(s));
		}
		return list2;
	}

	@Override
	public List<K> keys() {
		Set<String> list=jedisUtil.HASH.hkeys(key);
		List<K> list2=new ArrayList<K>();
		for(String s:list){
			list2.add(parseKey(s));
		}
		return list2;
	}

	@Override
	public long size() {
		return jedisUtil.HASH.hlen(key);
	}

	@Override
	public List<V> get(K[] ks) {
		String[] fieids=new String[ks.length];
		for(int i=0;i<ks.length;i++){
			fieids[i]=formatKey(ks[i]);
		}
		List<String> list=jedisUtil.HASH.hmget(key, fieids);
		List<V> list2=new ArrayList<V>();
		for(String s:list){
			list2.add(parse(s));
		}
		return list2;
	}
	@Override
	public void print() {
		Map<String, String> map=jedisUtil.HASH.hgetAll(key);
		logger.info("MAP:"+key+"==>size:"+map.size());
		for(String s:map.keySet()){
			logger.info("MAP:"+s+"-->"+map.get(s));
		}
	}
	
}
