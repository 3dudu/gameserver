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
import com.icegame.sysmanage.entity.SlaveNodes;
import com.icegame.sysmanage.mapper.SlaveNodesMapper;
import com.icegame.sysmanage.service.ISlaveNodesService;

@Service
public class SlaveNodesService implements ISlaveNodesService {
	
	@Autowired
	private SlaveNodesMapper slaveNodesMapper;
	
	@Autowired
	private LogService logService;
	
	Log log = new Log();

	public PageInfo<SlaveNodes> getSlaveNodesList(SlaveNodes slaveNodes, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<SlaveNodes> slaveNodesList = slaveNodesMapper.getSlaveNodesList(slaveNodes);
		PageInfo<SlaveNodes> pageInfo = new PageInfo<SlaveNodes>(slaveNodesList);
		return pageInfo;
	}

	public List<SlaveNodes> getSlaveNodesListNoPage(){
		return slaveNodesMapper.getSlaveNodesListNoPage();
	}
	
	public List<SlaveNodes> checkExistSlaveNodeWithName(SlaveNodes slaveNodes) {
		return slaveNodesMapper.checkExistSlaveNodeWithName(slaveNodes);
	}

	public List<SlaveNodes> checkExistSlaveNodeWithIp(SlaveNodes slaveNodes) {
		return slaveNodesMapper.checkExistSlaveNodeWithIp(slaveNodes);
	}
	
	public SlaveNodes getSlaveNodesById(Long id) {
		return slaveNodesMapper.getSlaveNodesById(id);
	}

	public SlaveNodes getSlaveNodesByIP(String ip) {
		return slaveNodesMapper.getSlaveNodesByIP(ip);
	}
	
	public boolean addSlaveNodes(SlaveNodes slaveNodes) {
		log = UserUtils.recording("添加slave节点["+slaveNodes.getName()+"]",Type.ADD.getName());
		logService.addLog(log);
		return slaveNodesMapper.addSlaveNodes(slaveNodes);
	}

	public boolean delSlaveNodes(Long id) {
		log = UserUtils.recording("删除slave节点["+id+"]",Type.DELETE.getName());
		logService.addLog(log);
		return slaveNodesMapper.delSlaveNodes(id);
	}

	public boolean updateSlaveNodes(SlaveNodes slaveNodes) {
		log = UserUtils.recording("修改slave节点["+slaveNodes.getName()+"]",Type.UPDATE.getName());
		logService.addLog(log);
		return slaveNodesMapper.updateSlaveNodes(slaveNodes);
	}
	
}
