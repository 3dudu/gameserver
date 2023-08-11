package com.icegame.sysmanage.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.SlaveNodes;

public interface ISlaveNodesService {
	
	public PageInfo<SlaveNodes> getSlaveNodesList(SlaveNodes slaveNodes,PageParam pageParam);

	public List<SlaveNodes> getSlaveNodesListNoPage();
	
	public List<SlaveNodes> checkExistSlaveNodeWithName(SlaveNodes slaveNodes);
	
	public List<SlaveNodes> checkExistSlaveNodeWithIp(SlaveNodes slaveNodes);
	
	public SlaveNodes getSlaveNodesById(Long id);
	
	public SlaveNodes getSlaveNodesByIP(String ip);
	
	public boolean addSlaveNodes(SlaveNodes slaveNodes);
	
	public boolean delSlaveNodes(Long id);

	public boolean updateSlaveNodes(SlaveNodes slaveNodes);
	
}

