package com.xuegao.core.datastruct.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.xuegao.core.datastruct.LinkedMapData;

public abstract class AbsRedisCacheLinkedMapData<K, V> extends AbsRedisCacheData<V> implements LinkedMapData<K, V> {

	private AbsRedisCacheListData<K> listData=null;
	
	public AbsRedisCacheLinkedMapData(String key,String redisAlias) {
		super(key,redisAlias);
		listData=new AbsRedisCacheListData<K>("[linkedmap]"+key,redisAlias) {
			@Override
			protected String format(K t) {
				return formatKey(t);
			}

			@Override
			protected K parse(String format) {
				return parseKey(format);
			}
		};
		if(listData.size()!=this.size()){
			logger.warn("警告:LinkedMap存在脏数据,map.size()与list.size()数量不相符,key="+key);
		}
	}

	protected abstract String formatKey(K k);
	
	protected abstract K parseKey(String k);

	@Override
	public void remove(K k) {
		listData.remove(k);
		jedisUtil.HASH.hdel(key, formatKey(k));
	}

	@Override
	public void put(K k, V v) {
		//判断k是否存在
		if(!exists(k)){
			listData.add(k);
		}
		jedisUtil.HASH.hset(key, formatKey(k), format(v));
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
	public void put(Map<K, V> map) {
		if(map==null||map.size()==0){
			return;
		}
		Map<String, String> newmap=new HashMap<String, String>();
		for(Entry<K, V> entry:map.entrySet()){
			if(!exists(entry.getKey())){
				listData.add(entry.getKey());
			}
			newmap.put(formatKey(entry.getKey()), format(entry.getValue()));
		}
		jedisUtil.HASH.hmset(key, newmap);
	}

	@Override
	public LinkedHashMap<K, V> getAll() {
		LinkedHashMap<K, V> newMap=new LinkedHashMap<K,V>();
		List<K> list=listData.getAll();
		Map<String, String> map=jedisUtil.HASH.hgetAll(key);
		for(K k:list){
			newMap.put(k, parse(map.get(formatKey(k))));
		}
		return newMap;
	}

	@Override
	public List<V> values() {
		List<K> list=listData.getAll();
		Map<String, String> map=jedisUtil.HASH.hgetAll(key);
		List<V> list2=new ArrayList<V>();
		for(K k:list){
			list2.add(parse(map.get(formatKey(k))));
		}
		return list2;
	}

	@Override
	public List<K> keys() {
		return listData.getAll();
	}

	@Override
	public long size() {
		return listData.size();
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
		List<K> list=listData.getAll();
		Map<String, String> map=jedisUtil.HASH.hgetAll(key);
		logger.info("LinkedMAP:"+key+"==>size:"+listData.size());
		for(K k:list){
			logger.info("LinkedMAP:"+formatKey(k)+"-->"+map.get(formatKey(k)));
		}
	}
	
	@Override
	public void clearKey() {
		listData.clearKey();
		super.clearKey();
	}
	
	@Override
	public void persist() {
		listData.persist();
		super.persist();
	}
	
	@Override
	public void setExpireTime(int seconds) {
		listData.setExpireTime(seconds);
		super.setExpireTime(seconds);
	}
}
