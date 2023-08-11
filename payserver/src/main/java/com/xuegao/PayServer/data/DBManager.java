package com.xuegao.PayServer.data;

import com.xuegao.core.db.DBWrapper;

public class DBManager {
	public static DBWrapper instance1=DBWrapper.getInstance("proxool.PayServer");
	
	public static DBWrapper getDB(){
		return instance1;
	}
}
