package com.icegame.sysmanage.service.impl;

import java.util.List;

import com.icegame.framework.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.Type;
import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.entity.SyncStatus;
import com.icegame.sysmanage.mapper.SyncStatusMapper;
import com.icegame.sysmanage.service.ISyncStatusService;

@Service
public class SyncStatusService implements ISyncStatusService {
	
	@Autowired
	private SyncStatusMapper syncStatusMapper;
	
	@Autowired
	private LogService logService;
	
	Log log = new Log();

	public PageInfo<SyncStatus> getSyncStatusList(SyncStatus syncStatus, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		if(syncStatus.getStatus().equals("选择同步状态"))syncStatus.setStatus("");
		if(syncStatus.getType().equals("选择同步类型"))syncStatus.setType("");
		List<SyncStatus> syncStatusList = syncStatusMapper.getSyncStatusList(syncStatus);
		PageInfo<SyncStatus> pageInfo = new PageInfo<SyncStatus>(syncStatusList);
		return pageInfo;
	}
	
	public List<SyncStatus> getTypeList() {
		return syncStatusMapper.getTypeList();
	}


	public List<SyncStatus> getStatusList() {
		return syncStatusMapper.getStatusList();
	}
	
	public boolean addSyncStatus(SyncStatus syncStatus) {
		log = UserUtils.recording("添加同步状态记录["+syncStatus.getServerNodeIp()+"]",Type.ADD.getName());
		logService.addLog(log);
		return syncStatusMapper.addSyncStatus(syncStatus);
	}

}
