package com.icegame.sysmanage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.sysmanage.entity.HelpSysSign;
import com.icegame.sysmanage.mapper.HelpSysSignMapper;
import com.icegame.sysmanage.service.IHelpSysSignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HelpSysSignService implements IHelpSysSignService {
	
	@Autowired
	private HelpSysSignMapper helpSysSignMapper;

	@Override
	public PageInfo<HelpSysSign> getHelpSysSignList(HelpSysSign helpSysSign, PageParam pageParam) {
		PageHelper.startPage(pageParam.getPageNo(),pageParam.getPageSize());
		List<HelpSysSign> helpSysSignList = helpSysSignMapper.getHelpSysSignList(helpSysSign);
		PageInfo<HelpSysSign> pageInfo = new PageInfo<HelpSysSign>(helpSysSignList);
		return pageInfo;
	}

	@Override
	public List<HelpSysSign> getSignTypeList(HelpSysSign helpSysSign) {
		return helpSysSignMapper.getHelpSysSignList(helpSysSign);
	}

	@Override
	public List<HelpSysSign> checkExistSignType(HelpSysSign helpSysSign) {
		return helpSysSignMapper.checkExistSignType(helpSysSign);
	}

	@Override
	public HelpSysSign getHelpSysSignById(Long id) {
		return helpSysSignMapper.getHelpSysSignById(id);
	}

	@Override
	public boolean addHelpSysSign(HelpSysSign helpSysSign) {
		return helpSysSignMapper.addHelpSysSign(helpSysSign);
	}

	@Override
	public boolean delHelpSysSign(Long id) {
		return helpSysSignMapper.delHelpSysSign(id);
	}

	@Override
	public boolean updateHelpSysSign(HelpSysSign helpSysSign) {
		return helpSysSignMapper.updateHelpSysSign(helpSysSign);
	}
}
