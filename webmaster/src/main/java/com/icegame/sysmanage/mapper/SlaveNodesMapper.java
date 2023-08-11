package com.icegame.sysmanage.mapper;

import java.util.List;

import com.icegame.sysmanage.entity.SlaveNodes;

public interface SlaveNodesMapper {
		
	public List<SlaveNodes> getSlaveNodesList(SlaveNodes slaveNodes);
	
	public List<SlaveNodes> getSlaveNodesListNoPage();
	
	public List<SlaveNodes> checkExistSlaveNodeWithName(SlaveNodes slaveNodes);
	
	public List<SlaveNodes> checkExistSlaveNodeWithIp(SlaveNodes slaveNodes);
			
	public SlaveNodes getSlaveNodesById(Long id);
	
	public SlaveNodes getSlaveNodesByIP(String ip);
	
	public SlaveNodes getSlaveNodesByServerId(Long serverId);
	
	public boolean addSlaveNodes(SlaveNodes slaveNodes);
	
	public boolean delSlaveNodes(Long id);

	public boolean updateSlaveNodes(SlaveNodes slaveNodes);

}
