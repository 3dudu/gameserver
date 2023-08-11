package com.icegame.sysmanage.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.HelpSysType;

import java.util.List;

public interface IHelpSysTypeService {

	public PageInfo<HelpSysType> getHelpSysTypeList(HelpSysType helpSysType, PageParam pageParam);

	public List<HelpSysType> getDiffTypeList(HelpSysType helpSysType);

	public List<HelpSysType> checkExistDiffType(HelpSysType helpSysType);

	public HelpSysType getHelpSysTypeById(Long id);

	public boolean addHelpSysType(HelpSysType helpSysType);

	public boolean delHelpSysType(Long id);

	public boolean updateHelpSysType(HelpSysType helpSysType);
}

