package com.xuegao.LoginServer.data;

import com.xuegao.core.db.DBWrapper;

public class DBManager {
	public static DBWrapper instance1=DBWrapper.getInstance("proxool.GateWay");

	public static DBWrapper getDB(){
		return instance1;
	}
}
