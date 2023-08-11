package com.icegame.sysmanage.mapper;

import java.util.List;

import com.icegame.sysmanage.entity.OpclServerStatus;

public interface OpclServerStatusMapper {
		
	public List<OpclServerStatus> getOpclServerStatusList(OpclServerStatus opclServerStatus);
	
	public List<OpclServerStatus> getStatusList();
	
	public OpclServerStatus getOpclServerStatusById(Long id);
				
	public boolean addOpclServerStatus(OpclServerStatus opclServerStatus);
	
	public boolean updateOpclServerStatus(OpclServerStatus opclServerStatus);
	
}
