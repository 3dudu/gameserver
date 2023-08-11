package com.icegame.gm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.gm.entity.Gmlogs;
import com.icegame.gm.mapper.GmlogsMapper;
import com.icegame.gm.service.IGmlogsService;

@Service
public class GmlogsService implements IGmlogsService {
	
	@Autowired
	private GmlogsMapper gmlogsMapper;

	public PageInfo<Gmlogs> getGmlogsList(Gmlogs gmlogs, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		if(gmlogs.getType().equals("选择操作类型"))gmlogs.setType("");
		List<Gmlogs> gmlogsList = gmlogsMapper.getGmlogsList(gmlogs);
		PageInfo<Gmlogs> pageInfo = new PageInfo<Gmlogs>(gmlogsList);
		return pageInfo;
	}

	public List<Gmlogs> getTypeList() {
		return gmlogsMapper.getTypeList();
	}

	public Gmlogs getGmlogsById(Long id) {
		return gmlogsMapper.getGmlogsById(id);
	}

	public boolean addGmlogs(Gmlogs gmlogs) {
		return gmlogsMapper.addGmlogs(gmlogs);
	}

	public boolean delGmlogs(Long id) {
		return gmlogsMapper.delGmlogs(id);
	}

	public boolean updateGmlogs(Gmlogs gmlogs) {
		return gmlogsMapper.updateGmlogs(gmlogs);
	}
	
}
