package com.icegame.sysmanage.mapper;

import java.util.List;

import com.icegame.sysmanage.entity.ServerNodes;

public interface ServerNodesMapper {
		
	public List<ServerNodes> getServerNodes(ServerNodes serverNodes);
	
	public List<ServerNodes> getServerNodesSync();
		
	public ServerNodes getServerNodesById(Long id);
	
	public boolean addServerNodes(ServerNodes serverNodes);
	
	public boolean delServerNodes(Long id);

	public boolean updateServerNodes(ServerNodes serverNodes);

}
