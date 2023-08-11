package com.icegame.sysmanage.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.SyncStatus;

public interface ISyncStatusService {
	
	public PageInfo<SyncStatus> getSyncStatusList(SyncStatus syncStatus,PageParam pageParam);
	
	public List<SyncStatus> getTypeList();
	
	public List<SyncStatus> getStatusList();
		
	public boolean addSyncStatus(SyncStatus syncStatus);

}

