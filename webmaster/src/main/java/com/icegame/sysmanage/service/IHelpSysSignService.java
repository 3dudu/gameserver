package com.icegame.sysmanage.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.HelpSysSign;

import java.util.List;

public interface IHelpSysSignService {

	public PageInfo<HelpSysSign> getHelpSysSignList(HelpSysSign helpSysSign, PageParam pageParam);

	public List<HelpSysSign> getSignTypeList(HelpSysSign helpSysSign);

	public List<HelpSysSign> checkExistSignType(HelpSysSign helpSysSign);

	public HelpSysSign getHelpSysSignById(Long id);

	public boolean addHelpSysSign(HelpSysSign helpSysSign);

	public boolean delHelpSysSign(Long id);

	public boolean updateHelpSysSign(HelpSysSign helpSysSign);
}

