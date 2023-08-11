package com.xuegao.core.datastruct.cacheinitial;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.xuegao.core.datastruct.cache.AbsRedisCacheZSetData;
import com.xuegao.core.datastruct.util.RedisSyncLock;

public abstract class AbsRedisInitialCacheZSetData<T> extends AbsRedisCacheZSetData<T>{
	
	public AbsRedisInitialCacheZSetData(String key,String redisAlias) {
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
				Map<T,Double> map=initialData();
				if("none".equals(this.type())){
					if(map.size()==0){
						markBlank();
					}else {
						super.add(map);
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
	/** 缓存初始化的数据(一般从数据库中获取) */
	public abstract Map<T,Double> initialData();
	
	@Override
	public void add(Map<T, Double> scores) {
		initial();
		if(isBlank()){
			super.clearKey();
		}
		super.add(scores);
	}
	
	@Override
	public void add(T t, double score) {
		initial();
		if(isBlank()){
			super.clearKey();
		}
		super.add(t, score);
	}
	
	@Override
	public double incrby(T t, double score) {
		initial();
		if(isBlank()){
			super.clearKey();
		}
		return super.incrby(t, score);
	}
	
	@Override
	public void remove(T t) {
		initial();
		if(isBlank()){
			return;
		}
		super.remove(t);
		if(!existKey()){
			markBlank();
		}
	}
	
	@Override
	public long removeByRank(int start, int end) {
		initial();
		if(isBlank()){
			return 0;
		}
		long count=super.removeByRank(start, end);
		if(!existKey()){
			markBlank();
		}
		return count;
	}
	
	@Override
	public long removeByScore(double min, double max) {
		initial();
		if(isBlank()){
			return 0;
		}
		long count=super.removeByScore(min, max);
		if(!existKey()){
			markBlank();
		}
		return count;
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
	public long size(double min, double max) {
		initial();
		if(isBlank()){
			return 0;
		}
		return super.size(min,max);
	}
	
	@Override
	public Set<T> getRangeOrderByScore(int start, int end) {
		initial();
		if(isBlank()){
			return new LinkedHashSet<T>();
		}
		return super.getRangeOrderByScore(start, end);
	}
	@Override
	public LinkedHashMap<T, Double> getRangeMapOrderByScore(int start, int end) {
		initial();
		if(isBlank()){
			return new LinkedHashMap<T,Double>();
		}
		return super.getRangeMapOrderByScore(start, end);
	}
	
	@Override
	public Set<T> getRange(double min, double max) {
		initial();
		if(isBlank()){
			return new LinkedHashSet<T>();
		}
		return super.getRange(min, max);
	}
	
	@Override
	public LinkedHashMap<T, Double> getRangeMap(double min, double max) {
		initial();
		if(isBlank()){
			return new LinkedHashMap<T,Double>();
		}
		return super.getRangeMap(min, max);
	}
	
	@Override
	public Set<T> getRangeOrderByScoreDesc(int start, int end) {
		initial();
		if(isBlank()){
			return new LinkedHashSet<T>();
		}
		return super.getRangeOrderByScoreDesc(start, end);
	}
	
	@Override
	public LinkedHashMap<T, Double> getRangeMapOrderByScoreDesc(int start,
			int end) {
		initial();
		if(isBlank()){
			return new LinkedHashMap<T,Double>();
		}
		return super.getRangeMapOrderByScoreDesc(start, end);
	}
	@Override
	public Long rank(T t) {
		initial();
		if(isBlank()){
			return null;
		}
		return super.rank(t);
	}
	@Override
	public Long revrank(T t) {
		initial();
		if(isBlank()){
			return null;
		}
		return super.revrank(t);
	}
	@Override
	public double score(T t) {
		initial();
		if(isBlank()){
			return -1;
		}
		return super.score(t);
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
			logger.info("ZSET:"+key+"==>size:"+0);
		}else {
			super.print();
		}
	}
	/////////////////////////////////////////////////////////////
	
	
	
	
}
