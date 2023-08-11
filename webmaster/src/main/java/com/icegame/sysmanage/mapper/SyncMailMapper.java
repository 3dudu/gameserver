package com.icegame.sysmanage.mapper;

import java.util.List;

import com.icegame.sysmanage.entity.SyncMail;

public interface SyncMailMapper {
		
	public List<SyncMail> getSyncMailList(SyncMail syncMail);
	
	public List<SyncMail> getStatusList();
	
	public SyncMail getSyncMailById(Long id);
				
	public boolean addSyncMail(SyncMail syncMail);
	
	public boolean updateSyncMail(SyncMail syncMail);
	
}
