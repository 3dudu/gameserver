package com.icegame.sysmanage.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.mapper.LogMapper;
import com.icegame.sysmanage.service.ILogService;

@Service
public class LogService implements ILogService {
	
	@Autowired
	private LogMapper logMapper;

	public PageInfo<Log> getLogList(Log log, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		if(log.getType().equals("选择操作类型"))log.setType("");
		List<Log> serverList = logMapper.getLogList(log);
		PageInfo<Log> pageInfo = new PageInfo<Log>(serverList);
		return pageInfo;
	}
	
	public List<Log> getTypeList() {
		return logMapper.getTypeList();
	}

	public Log getLogById(Long id) {
		return logMapper.getLogById(id);
	}

	public boolean addLog(Log log) {
		return logMapper.addLog(log);
	}

	public boolean delLog(Long id) {
		return logMapper.delLog(id);
	}

	public boolean updateLog(Log log) {
		return logMapper.updateLog(log);
	}

}
