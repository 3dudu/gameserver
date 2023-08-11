package com.icegame.gm.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.gm.entity.Gmlogs;

public interface IGmlogsService {
	
	public PageInfo<Gmlogs> getGmlogsList(Gmlogs gmlogs,PageParam pageParam);
	
	public List<Gmlogs> getTypeList();
	
	public Gmlogs getGmlogsById(Long id);
	
	public boolean addGmlogs(Gmlogs log);
	
	public boolean delGmlogs(Long id);

	public boolean updateGmlogs(Gmlogs log);
}

