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
import com.icegame.sysmanage.entity.PfOptions;
import com.icegame.sysmanage.mapper.PfOptionsMapper;
import com.icegame.sysmanage.service.IPfOptionsService;

@Service
public class PfOptionsService implements IPfOptionsService {
	
	@Autowired
	private PfOptionsMapper pfOptionsMapper;
	
	@Autowired
	private LogService logService;
	
	Log log = new Log();

	public PageInfo<PfOptions> getPfOptionsList(PfOptions pfOptions, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<PfOptions> pfOptionsList = pfOptionsMapper.getPfOptionsList(pfOptions);
		PageInfo<PfOptions> pageInfo = new PageInfo<PfOptions>(pfOptionsList);
		return pageInfo;
	}

	public PfOptions getPfOptionsById(Long id) {
		return pfOptionsMapper.getPfOptionsById(id);
	}

	public List<PfOptions> checkExistPfOptions(PfOptions pfOptions) {
		return pfOptionsMapper.checkExistPfOptions(pfOptions);
	}
	
	public List<PfOptions> getPfOptionsAll() {
		return pfOptionsMapper.getPfOptionsAll();
	}

	public Long addPfOptionsReturnId(PfOptions pfOptions) {
		log = UserUtils.recording("添加["+pfOptions.getKey()+"]接口配置",Type.ADD.getName());
		logService.addLog(log);
		return pfOptionsMapper.addPfOptionsReturnId(pfOptions);
	}
	
	public boolean addPfOptions(PfOptions pfOptions) {
		log = UserUtils.recording("添加["+pfOptions.getKey()+"]接口配置",Type.ADD.getName());
		logService.addLog(log);
		return pfOptionsMapper.addPfOptions(pfOptions);
	}

	public boolean delPfOptions(Long id) {
		log = UserUtils.recording("删除["+id+"]接口配置",Type.DELETE.getName());
		logService.addLog(log);
		return pfOptionsMapper.delPfOptions(id);
	}

	public boolean updatePfOptions(PfOptions pfOptions) {
		log = UserUtils.recording("修改["+pfOptions.getKey()+"]接口配置",Type.UPDATE.getName());
		logService.addLog(log);
		return pfOptionsMapper.updatePfOptions(pfOptions);
	}

}
