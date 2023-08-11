package com.xuegao.core.db.po;

public class SqlAndParamVo {
	String sql;
	Object[] params;
	
	public SqlAndParamVo(String sql, Object[] params) {
		super();
		this.sql = sql;
		this.params = params;
	}
	public SqlAndParamVo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public Object[] getParams() {
		return params;
	}
	public void setParams(Object[] params) {
		this.params = params;
	}
	
}
