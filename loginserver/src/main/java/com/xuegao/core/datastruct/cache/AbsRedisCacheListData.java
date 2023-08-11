package com.xuegao.core.datastruct.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import redis.clients.jedis.BinaryClient.LIST_POSITION;

import com.xuegao.core.datastruct.ListData;

public abstract class AbsRedisCacheListData<T> extends AbsRedisCacheData<T> implements ListData<T>{
	
	public AbsRedisCacheListData(String key,String redisAlias) {
		super(key,redisAlias);
	}

	@Override
	public void set(int index, T t) {
		jedisUtil.LISTS.lset(key, index, format(t));
	}

	@Override
	public long add(T t) {
		return jedisUtil.LISTS.rpush(key, format(t));
	}
	
	@Override
	public void add(int index, T t) {
		if(index==0){
			jedisUtil.LISTS.lpush(key, format(t));
		}else {
			String pre=jedisUtil.LISTS.lindex(key, index-1);
			jedisUtil.LISTS.linsert(key, LIST_POSITION.AFTER, pre, format(t));
		}
	}
	
	@Override
	public T popFromHead() {
		String value=jedisUtil.LISTS.lpop(key);
		return parse(value);
	}
	
	@Override
	public T popFromTail() {
		String value=jedisUtil.LISTS.rpop(key);
		return parse(value);
	}

	@Override
	public void addAll(Collection<T> collection) {
		if(collection!=null&&collection.size()>0){
			String[] strings=new String[collection.size()];
			Iterator<T> iterator=collection.iterator();
			int index=0;
			while (iterator.hasNext()) {
				T t = (T) iterator.next();
				strings[index++]=format(t);
			}
			jedisUtil.LISTS.rmpush(key, strings);
		}
	}

	@Override
	public void remove(T t) {
		jedisUtil.LISTS.lrem(key, 0, format(t));
	}

	@Override
	public void remove(int index) {
		jedisUtil.LISTS.lset(key, index, "DATA_WILL_REMOVED_IMMEDERTLY");
		jedisUtil.LISTS.lrem(key, 0, "DATA_WILL_REMOVED_IMMEDERTLY");
	}

	@Override
	public T get(int index) {
		String value=jedisUtil.LISTS.lindex(key, index);
		return parse(value);
	}

	@Override
	public List<T> getAll() {
		List<String> list=jedisUtil.LISTS.lrange(key, 0, -1);
		List<T> rList = new ArrayList<T>();
		for(String s:list){
			rList.add(parse(s));
		}
		return rList;
	}

	@Override
	public List<T> getRange(int startIndex, int size) {
		List<String> list=jedisUtil.LISTS.lrange(key, startIndex, startIndex+size-1);
		List<T> rList = new ArrayList<T>();
		for(String s:list){
			rList.add(parse(s));
		}
		return rList;
	}

	@Override
	public long size() {
		return jedisUtil.LISTS.llen(key);
	}

	@Override
	public void print() {
		List<String> list=jedisUtil.LISTS.lrange(key, 0, -1);
		logger.info("LIST:"+key+"==>size:"+list.size());
		for(int i=0;i<list.size();i++){
			logger.info("LIST:"+i+"-->"+list.get(i));
		}
	}

	@Override
	public void trim(int start, int end) {
		jedisUtil.LISTS.ltrim(key, start, end);
	}

}
