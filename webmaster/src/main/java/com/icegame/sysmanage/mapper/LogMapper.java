package com.icegame.sysmanage.mapper;

import java.util.List;

import com.icegame.sysmanage.entity.Log;

public interface LogMapper {
		
	public List<Log> getLogList(Log log);
	
	public List<Log> getTypeList();
		
	public Log getLogById(Long id);
	
	public boolean addLog(Log log);
	
	public boolean delLog(Long id);

	public boolean updateLog(Log log);

}
