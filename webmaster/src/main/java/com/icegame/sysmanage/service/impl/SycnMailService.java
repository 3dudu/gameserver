package com.icegame.sysmanage.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.entity.SyncMail;
import com.icegame.sysmanage.mapper.SyncMailMapper;
import com.icegame.sysmanage.service.ISyncMailService;

@Service
public class SycnMailService implements ISyncMailService {
	
	@Autowired
	private SyncMailMapper syncMailMapper;
	
	Log log = new Log();
	

	public PageInfo<SyncMail> getSyncMailList(SyncMail syncMail, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		if(syncMail.getType().equals("选择同步类型"))syncMail.setType("");
		if(syncMail.getStatus().equals("选择同步状态"))syncMail.setStatus("");
		List<SyncMail> syncMailList = syncMailMapper.getSyncMailList(syncMail);
		PageInfo<SyncMail> pageInfo = new PageInfo<SyncMail>(syncMailList);
		return pageInfo;
	}
	
	public List<SyncMail> getStatusList() {
		return syncMailMapper.getStatusList();
	}

	public SyncMail getSyncMailById(Long id) {
		return syncMailMapper.getSyncMailById(id);
	}

	public boolean addSyncMail(SyncMail syncMail) {
		return syncMailMapper.addSyncMail(syncMail);
	}

	public boolean updateSyncMail(SyncMail syncMail) {
		return syncMailMapper.updateSyncMail(syncMail);
	}
}
