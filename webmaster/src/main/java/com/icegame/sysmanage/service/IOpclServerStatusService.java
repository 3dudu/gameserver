package com.icegame.sysmanage.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.OpclServerStatus;

public interface IOpclServerStatusService {
	
	public PageInfo<OpclServerStatus> getOpclServerStatusList(OpclServerStatus opclServerStatus,PageParam pageParam);
	
	public List<OpclServerStatus> getStatusList();
	
	public OpclServerStatus getOpclServerStatusById(Long id);
	
	public boolean addOpclServerStatus(OpclServerStatus opclServerStatus);
	
	public boolean updateOpclServerStatus(OpclServerStatus opclServerStatus);
}

