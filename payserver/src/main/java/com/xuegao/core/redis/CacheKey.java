package com.xuegao.core.redis;


public class CacheKey {
	private int dbIndex;
	private String key;

	public CacheKey(String key,int dbIndex) {
		super();
		this.key = key;
		this.dbIndex=dbIndex;
	}

	@Override
	public String toString() {
		return key;
	}
	
	public int dbIndex(){
		return this.dbIndex;
	}
	
}
