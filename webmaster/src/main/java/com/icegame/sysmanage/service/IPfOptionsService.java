package com.icegame.sysmanage.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.PfOptions;

public interface IPfOptionsService {
	
	public PageInfo<PfOptions> getPfOptionsList(PfOptions pfOptions,PageParam pageParam);
	
	public PfOptions getPfOptionsById(Long id);
	
	public List<PfOptions> checkExistPfOptions(PfOptions pfOptions);
	
	public List<PfOptions> getPfOptionsAll();
	
	public boolean addPfOptions(PfOptions pfOptions);
	
	public Long addPfOptionsReturnId(PfOptions pfOptions);
	
	public boolean delPfOptions(Long id);

	public boolean updatePfOptions(PfOptions pfOptions);
}

