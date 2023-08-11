package com.icegame.sysmanage.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.HelpSys;

import java.util.List;

public interface IHelpSysService {

	public PageInfo<HelpSys> getHelpSysList(HelpSys helpSys, PageParam pageParam);

	public List<HelpSys> getAllOfficalList(HelpSys helpSys);

	public List<HelpSys> getAllStrategyList(HelpSys helpSys);

	public HelpSys getHelpSysById(Long id);

	public boolean addHelpSys(HelpSys helpSys);

	public boolean delHelpSys(Long id);

	public boolean updateHelpSys(HelpSys helpSys);

	public boolean clickZan(Long id);

}

