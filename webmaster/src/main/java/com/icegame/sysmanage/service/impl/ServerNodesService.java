package com.icegame.sysmanage.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.framework.utils.Type;
import com.icegame.framework.utils.UserUtils;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.entity.ServerNodes;
import com.icegame.sysmanage.mapper.ServerNodesMapper;
import com.icegame.sysmanage.service.IServerNodesService;

@Service
public class ServerNodesService implements IServerNodesService {

	@Autowired
	private ServerNodesMapper serverNodesMapper;

	@Autowired
	private LogService logService;

	Log log = new Log();

	public PageInfo<ServerNodes> getServerNodes(ServerNodes serverNodes, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<ServerNodes> serverNodesList = serverNodesMapper.getServerNodes(serverNodes);
		PageInfo<ServerNodes> pageInfo = new PageInfo<ServerNodes>(serverNodesList);
		return pageInfo;
	}

	public List<ServerNodes> getServerNodesSync(){
		return serverNodesMapper.getServerNodesSync();
	}

	public ServerNodes getServerNodesById(Long id) {
		return serverNodesMapper.getServerNodesById(id);
	}

	public boolean addServerNodes(ServerNodes serverNodes) {
		log = UserUtils.recording("添加服务器节点["+serverNodes.getName()+"]",Type.ADD.getName());
		logService.addLog(log);
		return serverNodesMapper.addServerNodes(serverNodes);
	}

	public boolean delServerNodes(Long id) {
		log = UserUtils.recording("删除服务器节点["+id+"]",Type.DELETE.getName());
		logService.addLog(log);
		return serverNodesMapper.delServerNodes(id);
	}

	public boolean updateServerNodes(ServerNodes serverNodes) {
		log = UserUtils.recording("修改服务器节点["+serverNodes.getName()+"]",Type.DELETE.getName());
		logService.addLog(log);
		return serverNodesMapper.updateServerNodes(serverNodes);
	}

	public ServerNodes findAnyServer(int serverType) {
		List<ServerNodes> serverNodesSync = getServerNodesSync();
		for (ServerNodes sn : serverNodesSync) {
			if (sn.getDiff() == serverType) {
				return sn;
			}
		}
		return null;
	}

}
