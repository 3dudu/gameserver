package com.xuegao.core.db.cache;

import java.util.List;

public interface IDBOperator {
	public List<Object[]> query(String sql,Object...params) throws Exception;
	
	public int execute(String sql,Object...params) throws Exception;
}
