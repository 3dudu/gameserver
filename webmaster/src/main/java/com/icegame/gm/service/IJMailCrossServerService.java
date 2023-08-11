package com.icegame.gm.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.gm.entity.JMailMessage;

public interface IJMailCrossServerService {
	
	public PageInfo<JMailMessage> getJMCSList(JMailMessage jmcs,PageParam pageParam);
	
	public JMailMessage getJMCSById(Long id);
	
	public boolean addJMCS(JMailMessage jmcs);
}

