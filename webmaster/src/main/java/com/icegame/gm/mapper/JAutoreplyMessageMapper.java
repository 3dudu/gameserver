package com.icegame.gm.mapper;

import com.icegame.gm.entity.JAutoreplyMessage;

import java.util.List;

public interface JAutoreplyMessageMapper {
		
	public List<JAutoreplyMessage> getJAutoreplyMessageList(JAutoreplyMessage jam);
		
	public JAutoreplyMessage getJAutoreplyMessageById(Long id);
	
	public boolean addJAutoreplyMessage(JAutoreplyMessage jam);
	
	public boolean updateJAutoreplyMessage(JAutoreplyMessage jam);
	
	public boolean delJAutoreplyMessage(Long id);

}
