package com.xuegao.core.db;

public class DBException extends RuntimeException{
	private static final long serialVersionUID = 1L;
//	String msg=null;
	public DBException(String sql,Object[] params,Exception e){
		super(e);
//		StringBuilder sb=new StringBuilder();
//		if(sql!=null)sb.append(sql);
//		if(params!=null){
//			for(int i=0;i<params.length;i++){
//				sb.append("<"+params[i]+">");
//			}
//		}
//		msg=sb.toString();
	}
	
	
	@Override
	public String getMessage() {
		return "DataBase operation failure.";
	}
}
