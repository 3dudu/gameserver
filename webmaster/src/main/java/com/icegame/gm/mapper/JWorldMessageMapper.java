package com.icegame.gm.mapper;

import java.util.List;

import com.icegame.gm.entity.JWorldMessage;

public interface JWorldMessageMapper {
		
	public List<JWorldMessage> getJMessageList(JWorldMessage jmessage);
		
	public JWorldMessage getJMessageById(Long id);
	
	public boolean addJMessage(JWorldMessage jmessage);

}
