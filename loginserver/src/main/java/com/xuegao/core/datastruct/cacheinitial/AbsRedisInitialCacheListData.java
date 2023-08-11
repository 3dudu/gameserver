package com.xuegao.core.datastruct.cacheinitial;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.xuegao.core.datastruct.cache.AbsRedisCacheListData;
import com.xuegao.core.datastruct.util.RedisSyncLock;
import com.xuegao.core.exception.DeException;

public abstract class AbsRedisInitialCacheListData<T> extends AbsRedisCacheListData<T>{
	
	public AbsRedisInitialCacheListData(String key,String redisAlias) {
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
				List<T> list=initialData();
				if("none".equals(this.type())){
					if(list.size()==0){
						markBlank();
					}else {
						super.addAll(list);
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
	
	public abstract List<T> initialData();

	@Override
	public void set(int index, T t) {
		initial();
		if(!isBlank()){
			super.set(index, t);
		}else {
			throw new DeException("can not set index in blank list");
		}
	}

	@Override
	public long add(T t) {
		initial();
		if(isBlank()){
			super.clearKey();
		}
		return super.add(t);
	}
	
	@Override
	public void addAll(Collection<T> collection) {
		initial();
		if(collection!=null&&collection.size()>0){
			if(isBlank()){
				super.clearKey();
			}
			super.addAll(collection);
		}
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
	public void remove(int index) {
		initial();
		if(isBlank()){
			throw new DeException("blank list,index out of range");
		}
		super.remove(index);
		if(!existKey()){
			markBlank();
		}
	}

	@Override
	public T get(int index) {
		initial();
		if(isBlank()){
			throw new DeException("this is blank list,index out of range");
		}
		return super.get(index);
	}

	@Override
	public List<T> getAll() {
		initial();
		if(isBlank()){
			return new ArrayList<T>();
		}
		return super.getAll();
	}

	@Override
	public List<T> getRange(int startIndex, int size) {
		initial();
		if(isBlank()){
			return new ArrayList<T>();
		}
		return super.getRange(startIndex, size);
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
	public void add(int index, T t) {
		initial();
		if(isBlank()){
			if(index==0){
				super.add(index, t);
			}else {
				throw new DeException("index out of range");
			}
		}else {
			super.add(index,t);
		}
	}
	
	@Override
	public T popFromHead() {
		initial();
		if(isBlank()){
			return null;
		}
		return super.popFromHead();
	}
	
	@Override
	public T popFromTail() {
		initial();
		if(isBlank()){
			return null;
		}
		return super.popFromTail();
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
			logger.info("LIST:"+key+"==>size:"+0);
		}else{
			super.print();
		}
		
	}
	
	@Override
	public void trim(int start, int end) {
		initial();
		if(isBlank()){
			return;
		}
		super.trim(start,end);
	}
}
