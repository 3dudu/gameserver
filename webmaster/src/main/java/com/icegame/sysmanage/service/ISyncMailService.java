package com.icegame.sysmanage.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.SyncMail;

public interface ISyncMailService {
	
	public PageInfo<SyncMail> getSyncMailList(SyncMail syncMail,PageParam pageParam);
	
	public List<SyncMail> getStatusList();
	
	public SyncMail getSyncMailById(Long id);
				
	public boolean addSyncMail(SyncMail syncMail);
	
	public boolean updateSyncMail(SyncMail syncMail);
}

