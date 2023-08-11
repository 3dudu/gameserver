package com.xuegao.core.db;

import java.sql.Connection;

public class TransactionConnection {
	private Connection connection;
	
	private String tranBeginClassname;
	private String tranBeginMethodName;
	private int tranBeginNum;
	public TransactionConnection() {
		
	}
	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public String getTranBeginClassname() {
		return tranBeginClassname;
	}
	public void setTranBeginClassname(String tranBeginClassname) {
		this.tranBeginClassname = tranBeginClassname;
	}
	public String getTranBeginMethodName() {
		return tranBeginMethodName;
	}
	public void setTranBeginMethodName(String tranBeginMethodName) {
		this.tranBeginMethodName = tranBeginMethodName;
	}
	public int getTranBeginNum() {
		return tranBeginNum;
	}
	public void setTranBeginNum(int tranBeginNum) {
		this.tranBeginNum = tranBeginNum;
	}
}
