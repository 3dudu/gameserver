package com.icegame.sysmanage.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.ServerNodes;

public interface IServerNodesService {

	public PageInfo<ServerNodes> getServerNodes(ServerNodes serverNodes,PageParam pageParam);

	public List<ServerNodes> getServerNodesSync();

	public ServerNodes getServerNodesById(Long id);

	public boolean addServerNodes(ServerNodes serverNodes);

	public boolean delServerNodes(Long id);

	public boolean updateServerNodes(ServerNodes serverNodes);

	ServerNodes findAnyServer(int serverType);
}

