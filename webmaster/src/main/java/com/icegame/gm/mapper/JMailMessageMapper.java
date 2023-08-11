package com.icegame.gm.mapper;

import java.util.List;

import com.icegame.gm.entity.JMailMessage;

public interface JMailMessageMapper {
		
	public List<JMailMessage> getJMCSList(JMailMessage jmcs);
		
	public JMailMessage getJMCSById(Long id);
	
	public boolean addJMCS(JMailMessage jmcs);

}
