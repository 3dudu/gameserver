package com.icegame.sysmanage.mapper;

import java.util.List;

import com.icegame.sysmanage.entity.SyncStatus;

public interface SyncStatusMapper {
		
	public List<SyncStatus> getSyncStatusList(SyncStatus syncStatus);
	
	public List<SyncStatus> getTypeList();
	
	public List<SyncStatus> getStatusList();
				
	public boolean addSyncStatus(SyncStatus syncStatus);
	
}
