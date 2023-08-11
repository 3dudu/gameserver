package com.icegame.gm.service;

import com.github.pagehelper.PageInfo;
import com.icegame.framework.dto.PageParam;
import com.icegame.gm.entity.JWorldMessage;

public interface IJWorldMessageService {
	
	public PageInfo<JWorldMessage> getJMessageList(JWorldMessage jmessage,PageParam pageParam);
	
	public JWorldMessage getJMessage(Long id);
	
	public boolean addJMessage(JWorldMessage jmessage);
}

