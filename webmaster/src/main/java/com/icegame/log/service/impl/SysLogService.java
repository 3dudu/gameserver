package com.icegame.log.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.log.entity.SysLog;
import com.icegame.log.mapper.SysLogMapper;
import com.icegame.log.service.ISysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysLogService implements ISysLogService {
	
	@Autowired
	private SysLogMapper sysLogMapper;

	@Override
	public PageInfo<SysLog> getLogList(SysLog sysLog, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<SysLog> sysLogList = sysLogMapper.getLogList(sysLog);
		PageInfo<SysLog> pageInfo = new PageInfo<SysLog>(sysLogList);
		return pageInfo;
	}

	@Override
	public void save(SysLog sysLog) {
		sysLogMapper.save(sysLog);
	}
}
