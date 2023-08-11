package com.icegame.gm.mapper;

import java.util.List;

import com.icegame.gm.entity.JPreinstallMessage;

public interface JPreinstallMessageMapper {
		
	public List<JPreinstallMessage> getJPreinstallMessageList(JPreinstallMessage jpm);
		
	public JPreinstallMessage getJPreinstallMessageById(Long id);
	
	public boolean addJPreinstallMessage(JPreinstallMessage jpm);
	
	public boolean updateJPreinstallMessage(JPreinstallMessage jpm);
	
	public boolean delJPreinstallMessage(Long id);

}
