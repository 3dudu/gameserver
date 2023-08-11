package com.icegame.sysmanage.mapper;

import com.icegame.sysmanage.entity.HelpSysSign;

import java.util.List;

public interface HelpSysSignMapper {
		
	public List<HelpSysSign> getHelpSysSignList(HelpSysSign helpSysSign);

	public List<HelpSysSign> checkExistSignType(HelpSysSign helpSysSign);

	public HelpSysSign getHelpSysSignById(Long id);
	
	public boolean addHelpSysSign(HelpSysSign helpSysSign);
	
	public boolean delHelpSysSign(Long id);

	public boolean updateHelpSysSign(HelpSysSign helpSysSign);

}
