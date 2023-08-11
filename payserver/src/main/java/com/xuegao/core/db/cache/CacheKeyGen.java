package com.xuegao.core.db.cache;

import com.alibaba.fastjson.JSONArray;

public class CacheKeyGen {
	boolean isAddKeygen=false;
	
	JSONArray array=new JSONArray();
	private CacheKeyGen(Object...params){
		if(params!=null){
			for(Object object:params){
				array.add(""+object);
			}
		}
	}
	
	public static CacheKeyGen get(Object... params){
		return new CacheKeyGen(params);
	}
	
	@Override
	public String toString() {
		return array.toString();
	}

}
