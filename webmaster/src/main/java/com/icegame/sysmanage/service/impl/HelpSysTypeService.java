package com.icegame.sysmanage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.HelpSysType;
import com.icegame.sysmanage.entity.Log;
import com.icegame.sysmanage.mapper.HelpSysTypeMapper;
import com.icegame.sysmanage.service.IHelpSysTypeService;
import com.icegame.sysmanage.service.ILogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HelpSysTypeService implements IHelpSysTypeService {
	
	@Autowired
	private HelpSysTypeMapper helpSysTypeMapper;

	@Override
	public PageInfo<HelpSysType> getHelpSysTypeList(HelpSysType helpSysType, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<HelpSysType> helpSysTypeList = helpSysTypeMapper.getHelpSysTypeList(helpSysType);
		PageInfo<HelpSysType> pageInfo = new PageInfo<HelpSysType>(helpSysTypeList);
		return pageInfo;
	}

	@Override
	public List<HelpSysType> getDiffTypeList(HelpSysType helpSysType) {
		return helpSysTypeMapper.getHelpSysTypeList(helpSysType);
	}

	@Override
	public List<HelpSysType> checkExistDiffType(HelpSysType helpSysType) {
		return helpSysTypeMapper.checkExistDiffType(helpSysType);
	}

	@Override
	public HelpSysType getHelpSysTypeById(Long id) {
		return helpSysTypeMapper.getHelpSysTypeById(id);
	}

	@Override
	public boolean addHelpSysType(HelpSysType helpSysType) {
		return helpSysTypeMapper.addHelpSysType(helpSysType);
	}

	@Override
	public boolean delHelpSysType(Long id) {
		return helpSysTypeMapper.delHelpSysType(id);
	}

	@Override
	public boolean updateHelpSysType(HelpSysType helpSysType) {
		return helpSysTypeMapper.updateHelpSysType(helpSysType);
	}
}
