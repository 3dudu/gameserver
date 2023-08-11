package com.xuegao.core.db.cache;

import java.util.List;


public abstract class CacheBasedDBMap {
	String tablename="table_cache_data";
	
	String key_key="key";
	String key_value="value";
	
	int tableCount=1;
	
	IDBOperator dbOperator;
	
	public CacheBasedDBMap(String tablename, int tableCount,
			IDBOperator dbOperator) {
		super();
		this.tablename = tablename;
		
		this.tableCount = tableCount;
		this.dbOperator = dbOperator;
	}

	public String get(CacheKeyGen key){
		String tname=getTableName(key);
		try {
			List<Object[]> list=dbOperator.query("select `"+key_value+"` from `"+tname+"` where `"+key_key+"`= ?", key.toString());
			if(list!=null&&list.size()>0){
				Object objects=list.get(0)[0];
				return ""+objects;
			}
		} catch (Exception e) {
//			e.printStackTrace();
			System.err.println("WARN: CacheBasedDBMap.get()--->"+e.getMessage());
		}
		return null;
	}
	
	public void put(CacheKeyGen key,String value){
		String tname=getTableName(key);
		
		String sql="replace into `"+tname+"`(`"+key_key+"`, `"+key_value+"`) values(?, ?)";
		try {
			dbOperator.execute(sql, key.toString(),value);
		} catch (Exception e) {
			createTableIfNotExist(tname);
			try {
				dbOperator.execute(sql, key.toString(),value);
			} catch (Exception e1) {
				System.err.println("WARN: CacheBasedDBMap.put()--->"+e.getMessage());
			}
			
		}
	}

	
	
	//------------------
	
	private String getTableName(CacheKeyGen keyGen){
		int index=getIndexByHash(keyGen);
		return tablename+"_"+index;
	}
	
	private int getIndexByHash(CacheKeyGen keyGen){
		return Math.abs(keyGen.toString().hashCode()%tableCount);
	}
	
	private  void createTableIfNotExist(String tname){
		String sql="CREATE TABLE if not exists `"+tname+"` (`"+key_key+"` varchar(255) NOT NULL,`"+key_value+"` text, PRIMARY KEY (`"+key_key+"`)) ENGINE=MyISAM DEFAULT CHARSET=utf8";
		try {
			dbOperator.execute(sql);
		} catch (Exception e) {
//			e.printStackTrace();
			System.err.println("WARN: CacheBasedDBMap.createTableIfNotExist()--->"+e.getMessage());
		}
	}
	
}
