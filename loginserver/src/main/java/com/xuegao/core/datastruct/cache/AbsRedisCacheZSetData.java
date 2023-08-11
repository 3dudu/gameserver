package com.xuegao.core.datastruct.cache;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.xuegao.core.datastruct.ZSetData;

public abstract class AbsRedisCacheZSetData<T> extends AbsRedisCacheData<T> implements ZSetData<T>{
	
	public AbsRedisCacheZSetData(String key,String redisAlias) {
		super(key,redisAlias);
	}

	@Override
	public void add(T t, double score) {
		jedisUtil.SORTSET.zadd(key, score, format(t));
	}

	@Override
	public void add(Map<T, Double> scores) {
		if(scores!=null){
			Map<String, Double> map=new HashMap<String, Double>();
			for(Entry<T, Double> entry:scores.entrySet()){
				map.put(format(entry.getKey()), entry.getValue());
			}
			jedisUtil.SORTSET.zadd(key, map);
		}
	}

	@Override
	public double incrby(T t, double score) {
		return jedisUtil.SORTSET.zincrby(key, score, format(t));
	}

	@Override
	public void remove(T t) {
		jedisUtil.SORTSET.zrem(key, format(t));
	}

	@Override
	public long removeByRank(int start, int end) {
		return jedisUtil.SORTSET.zremrangeByRank(key, start, end);
	}

	@Override
	public long removeByScore(double min, double max) {
		return jedisUtil.SORTSET.zremrangeByScore(key, min, max);
	}

	@Override
	public long size() {
		return jedisUtil.SORTSET.zcard(key);
	}

	@Override
	public long size(double min, double max) {
		return jedisUtil.SORTSET.zcount(key, min, max);
	}

	@Override
	public Set<T> getRangeOrderByScore(int start, int end) {
		Set<String> set=jedisUtil.SORTSET.zrange(key, start, end);
		Set<T> set2=new LinkedHashSet<T>();
		for(String s:set){
			set2.add(parse(s));
		}
		return set2;
	}

	@Override
	public LinkedHashMap<T, Double> getRangeMapOrderByScore(int start, int end) {
		Set<String> set=jedisUtil.SORTSET.zrange(key, start, end);
		LinkedHashMap<T,Double> map=new LinkedHashMap<T,Double>();
		for(String s:set){
			T t=parse(s);
			double score=score(t);
			map.put(t, score);
		}
		return map;
	}

	@Override
	public Set<T> getRange(double min, double max) {
		Set<String> set=jedisUtil.SORTSET.zrangeByScore(key, min, max);
		Set<T> set2=new LinkedHashSet<T>();
		for(String s:set){
			set2.add(parse(s));
		}
		return set2;
	}

	@Override
	public LinkedHashMap<T, Double> getRangeMap(double min, double max) {
		Set<String> set=jedisUtil.SORTSET.zrangeByScore(key, min, max);
		LinkedHashMap<T,Double> map=new LinkedHashMap<T,Double>();
		for(String s:set){
			T t=parse(s);
			double score=score(t);
			map.put(t, score);
		}
		return map;
	}

	@Override
	public Set<T> getRangeOrderByScoreDesc(int start, int end) {
		Set<String> set=jedisUtil.SORTSET.zrevrange(key, start, end);
		Set<T> set2=new LinkedHashSet<T>();
		for(String s:set){
			set2.add(parse(s));
		}
		return set2;
	}

	@Override
	public LinkedHashMap<T, Double> getRangeMapOrderByScoreDesc(int start,
			int end) {
		Set<String> set=jedisUtil.SORTSET.zrevrange(key, start, end);
		LinkedHashMap<T,Double> map=new LinkedHashMap<T,Double>();
		for(String s:set){
			T t=parse(s);
			double score=score(t);
			map.put(t, score);
		}
		return map;
	}

	@Override
	public Long rank(T t) {
		return jedisUtil.SORTSET.zrank(key, format(t));
	}

	@Override
	public Long revrank(T t) {
		return jedisUtil.SORTSET.zrevrank(key, format(t));
	}

	@Override
	public double score(T t) {
		return jedisUtil.SORTSET.zscore(key, format(t));
	}

}
