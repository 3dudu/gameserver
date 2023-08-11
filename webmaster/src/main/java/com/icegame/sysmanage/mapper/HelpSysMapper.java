package com.icegame.sysmanage.mapper;

import com.icegame.sysmanage.entity.HelpSys;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HelpSysMapper {
		
	public List<HelpSys> getHelpSysList(HelpSys helpSys);

	public List<HelpSys> getAllOfficalList(HelpSys helpSys);

	public List<HelpSys> getAllStrategyList(HelpSys helpSys);

	public HelpSys getHelpSysById(@Param("id") Long id);
	
	public boolean addHelpSys(HelpSys helpSys);
	
	public boolean delHelpSys(@Param("id") Long id);

	public boolean updateHelpSys(HelpSys helpSys);

	public boolean clickZan(@Param("id") Long id);

}
