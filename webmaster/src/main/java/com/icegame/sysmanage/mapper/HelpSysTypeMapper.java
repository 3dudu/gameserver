package com.icegame.sysmanage.mapper;

import com.icegame.sysmanage.entity.HelpSysType;

import java.util.List;

public interface HelpSysTypeMapper {
		
	public List<HelpSysType> getHelpSysTypeList(HelpSysType helpSysType);

	public List<HelpSysType> checkExistDiffType(HelpSysType helpSysType);

	public HelpSysType getHelpSysTypeById(Long id);
	
	public boolean addHelpSysType(HelpSysType helpSysType);
	
	public boolean delHelpSysType(Long id);

	public boolean updateHelpSysType(HelpSysType helpSysType);

}
