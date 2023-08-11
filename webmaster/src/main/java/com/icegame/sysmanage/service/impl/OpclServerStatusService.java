package com.icegame.sysmanage.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.entity.OpclServerStatus;
import com.icegame.sysmanage.mapper.OpclServerStatusMapper;
import com.icegame.sysmanage.service.IOpclServerStatusService;

@Service
public class OpclServerStatusService implements IOpclServerStatusService {
	
	@Autowired
	private OpclServerStatusMapper opclServerStatusMapper;
	
	Log log = new Log();
	
	public PageInfo<OpclServerStatus> getOpclServerStatusList(OpclServerStatus opclServerStatus, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		if(opclServerStatus.getType().equals("选择同步类型"))opclServerStatus.setType("");
		if(opclServerStatus.getStatus().equals("选择同步状态"))opclServerStatus.setStatus("");
		List<OpclServerStatus> opclServerStatusList = opclServerStatusMapper.getOpclServerStatusList(opclServerStatus);
		PageInfo<OpclServerStatus> pageInfo = new PageInfo<OpclServerStatus>(opclServerStatusList);
		return pageInfo;
	}
	
	public List<OpclServerStatus> getStatusList() {
		return opclServerStatusMapper.getStatusList();
	}
	
	public OpclServerStatus getOpclServerStatusById(Long id) {
		return opclServerStatusMapper.getOpclServerStatusById(id);
	}
	
	
	public boolean addOpclServerStatus(OpclServerStatus opclServerStatus) {
		return opclServerStatusMapper.addOpclServerStatus(opclServerStatus);
	}

	public boolean updateOpclServerStatus(OpclServerStatus opclServerStatus) {
		return opclServerStatusMapper.updateOpclServerStatus(opclServerStatus);
	}

	

}
