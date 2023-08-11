package com.icegame.datasources;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class MultipleDataSource extends AbstractRoutingDataSource{
	
	private static final ThreadLocal<String> CONTEXT_HOLDER = new InheritableThreadLocal<String>();
	
	public static void setDataSourceType(String dataSource) {
		CONTEXT_HOLDER.remove();
		CONTEXT_HOLDER.set(dataSource);
	}
	
	public static String getDataSourceType() {
		return CONTEXT_HOLDER.get();
		
	}
	
	public static void clearDataSourceType() {
		CONTEXT_HOLDER.remove();
	}

	@Override
	protected Object determineCurrentLookupKey() {
		return getDataSourceType();
	}

}
