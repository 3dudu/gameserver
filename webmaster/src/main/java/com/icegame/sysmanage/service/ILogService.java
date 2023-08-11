package com.icegame.sysmanage.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.Log;

public interface ILogService {
	
	public PageInfo<Log> getLogList(Log log,PageParam pageParam);
	
	public List<Log> getTypeList();
	
	public Log getLogById(Long id);
	
	public boolean addLog(Log log);
	
	public boolean delLog(Long id);

	public boolean updateLog(Log log);
}

